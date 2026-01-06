package site.techmoa.app.batch.rss

import com.apptasticsoftware.rssreader.Item
import com.apptasticsoftware.rssreader.RssReader
import com.apptasticsoftware.rssreader.filter.InvalidXmlCharacterFilter
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import site.techmoa.app.storage.db.entity.ArticleEntity
import site.techmoa.app.storage.db.entity.BlogEntity
import site.techmoa.app.storage.db.entity.BlogStatus
import site.techmoa.app.storage.db.repository.ArticleRepository
import site.techmoa.app.storage.db.repository.BlogRepository

@Component
class RssReaderCollector(
    private val articleRepository: ArticleRepository,
    private val blogRepository: BlogRepository
) : RssCollector {

    companion object {
        private const val STANDARD_LINK_PREFIX: String = "https"
    }
    private val log = LoggerFactory.getLogger(this.javaClass)

    @Transactional
    override fun execute() {
        // 1. 구독 블로그 조회
        log.info("Set Blogs List")
        val blogs = blogRepository.findAllStatus(status = BlogStatus.ACTIVE)

        // 2. 각 블로그 RSS 수집
        log.info("Start Collecting Articles")
        val rssReader = RssReader()
        val collectedItems = blogs.associateWith { blog ->
            rssReader.addFeedFilter(InvalidXmlCharacterFilter())
                .read(blog.rssLink).toList()
        }

        // 3. 새로운 아티클 필터링
        log.info("Filter New Articles")
        val newArticles = mutableListOf<ArticleEntity>()
        for ((blog, items) in collectedItems) {
            newArticles.addAll(filterNewArticles(items, blog))
        }

        // 4. 새로운 아티클 저장
        log.info("Save New Articles")
        if (newArticles.isNotEmpty()) {
            articleRepository.saveAll(newArticles)
        }
    }

    private fun filterNewArticles(items: List<Item>, blog: BlogEntity): List<ArticleEntity> {
        val filtered = items.mapNotNull { item -> toArticle(blog, item) }
            .filter { article -> isNewArticle(article) }
        return filtered
    }

    private fun toArticle(blog: BlogEntity, item: Item): ArticleEntity? {
        val blogId = blog.id
        val title = item.title.orElse(null) ?: return null
        val link = item.link.orElse(null) ?: return null
        val guid = item.guid.orElse(null) ?: link
        val pubDate = parseToEpochMillis(item) ?: return null

        val normalizedLink = normalizeLink(blog.link, link)

        return ArticleEntity.of(
            blogId = blogId,
            title = title,
            link = normalizedLink,
            guid = guid,
            pubDate = pubDate
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

    private fun isNewArticle(articleEntity: ArticleEntity): Boolean {
        return !articleRepository.existsByBlogIdAndGuid(articleEntity.blogId, articleEntity.guid)
    }
}
