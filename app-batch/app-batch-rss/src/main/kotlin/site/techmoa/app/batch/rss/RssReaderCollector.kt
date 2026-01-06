package site.techmoa.app.batch.rss

import com.apptasticsoftware.rssreader.Item
import com.apptasticsoftware.rssreader.RssReader
import com.apptasticsoftware.rssreader.filter.InvalidXmlCharacterFilter
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import site.techmoa.app.core.article.Article
import site.techmoa.app.core.article.ArticlePort
import site.techmoa.app.core.blog.Blog
import site.techmoa.app.core.blog.BlogPort
import site.techmoa.app.core.blog.BlogStatus

@Component
class RssReaderCollector(
    private val articlePort: ArticlePort,
    private val blogPort: BlogPort,
) : RssCollector {

    companion object {
        private const val STANDARD_LINK_PREFIX: String = "https"
    }
    private val log = LoggerFactory.getLogger(this.javaClass)

    @Transactional
    override fun execute() {
        // 1. 구독 블로그 조회
        log.info("Set Blogs List")
        val blogs = blogPort.findAllBy(status = BlogStatus.ACTIVE)

        // 2. 각 블로그 RSS 수집
        log.info("Start Collecting Articles")
        val rssReader = RssReader().addFeedFilter(InvalidXmlCharacterFilter())
        val collectedItems = blogs.associateWith { rssReader.read(it.rssLink).toList() }

        // 3. 새로운 아티클 필터링
        log.info("Filter New Articles")
        val newArticles = mutableListOf<Article>()
        for ((blog, items) in collectedItems) {
            newArticles.addAll(filterNewArticles(items, blog))
        }

        // 4. 새로운 아티클 저장
        log.info("Save New Articles")
        if (newArticles.isNotEmpty()) {
            articlePort.saveAll(newArticles)
        }
    }

    private fun filterNewArticles(items: List<Item>, blog: Blog): List<Article> {
        val filtered = items.mapNotNull { toArticle(blog, it) }
            .filter { isNewArticle(it) }
        return filtered
    }

    private fun toArticle(blog: Blog, item: Item): Article? {
        val blogId = blog.id
        val title = item.title.orElse(null) ?: return null
        val link = item.link.orElse(null) ?: return null
        val guid = item.guid.orElse(null) ?: link
        val pubDate = parseToEpochMillis(item) ?: return null

        val normalizedLink = normalizeLink(blog.link, link)

        return Article.of(
            blogId = blogId,
            title = title,
            link = normalizedLink,
            guid = guid,
            pubDate = pubDate,
        )
    }

    private fun normalizeLink(blogLink: String, link: String): String {
        if (link.startsWith(STANDARD_LINK_PREFIX)) {
            return link
        }
        return blogLink + link
    }

    private fun parseToEpochMillis(item: Item): Long? {
        return item.pubDateAsZonedDateTime.orElse(null)
            ?.toInstant()
            ?.toEpochMilli()
    }

    private fun isNewArticle(article: Article): Boolean {
        return !articlePort.existsByBlogIdAndGuid(article.blogId, article.guid)
    }
}
