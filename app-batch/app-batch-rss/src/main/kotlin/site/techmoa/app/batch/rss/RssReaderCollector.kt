package site.techmoa.app.batch.rss

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import site.techmoa.app.common.article.domain.Article
import site.techmoa.app.common.article.port.ArticlePort
import site.techmoa.app.common.blog.BlogPort
import site.techmoa.app.common.blog.BlogStatus

@Component
class RssReaderCollector(
    private val rssClient: RssClient,
    private val articlePort: ArticlePort,
    private val blogPort: BlogPort,
) : CollectorTemplate {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @Transactional
    override fun execute() {
        // 1. 구독 블로그 조회
        log.info("Set Blogs List")
        val blogs = blogPort.findAllBy(BlogStatus.ACTIVE)

        // 2. 각 블로그 RSS 수집
        log.info("Start Collecting Articles")
        val collectedArticles = blogs.flatMap { rssClient.fetch(it) }

        // 3. 새로운 아티클 필터링
        log.info("Filter New Articles")
        val newArticles = collectedArticles.filter { isNewArticle(it) }

        // 4. 새로운 아티클 저장
        log.info("Save New Articles")
        articlePort.saveAll(newArticles)
    }

    private fun isNewArticle(article: Article): Boolean {
        return !articlePort.existsByBlogIdAndGuid(article.blogId, article.guid)
    }
}
