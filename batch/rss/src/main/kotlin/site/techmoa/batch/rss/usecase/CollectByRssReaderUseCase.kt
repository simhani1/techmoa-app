package site.techmoa.batch.rss.usecase

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import site.techmoa.batch.rss.domain.model.BlogStatus
import site.techmoa.batch.rss.port.ArticlePort
import site.techmoa.batch.rss.port.BlogPort
import site.techmoa.batch.rss.support.RssClient
import site.techmoa.batch.rss.trigger.CollectRssUseCase

@Component
class CollectByRssReaderUseCase(
    private val rssClient: RssClient,
    private val articlePort: ArticlePort,
    private val blogPort: BlogPort
) : CollectRssUseCase {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @Transactional
    override fun execute() {
        // 1. 구독 블로그 조회
        log.info("[${this.javaClass.simpleName}] Select Blogs List")
        val blogs = blogPort.findAllBy(BlogStatus.ACTIVE)

        // 2. 각 블로그 RSS에서 아티클 수집
        log.info("[${this.javaClass.simpleName}] Start Collecting Articles from RSS")
        val articles = blogs.flatMap { rssClient.fetch(it) }

        // 3. 아티클 저장
        log.info("[${this.javaClass.simpleName}] Save New Articles")
        articlePort.saveAllIgnoringDuplicates(articles)
    }
}
