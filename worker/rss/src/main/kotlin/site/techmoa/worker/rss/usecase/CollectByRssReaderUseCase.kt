package site.techmoa.worker.rss.usecase

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import site.techmoa.worker.rss.domain.model.Article
import site.techmoa.worker.rss.domain.model.Blog
import site.techmoa.worker.rss.domain.model.BlogStatus
import site.techmoa.worker.rss.port.ArticlePort
import site.techmoa.worker.rss.port.BlogPort
import site.techmoa.worker.rss.support.RssClient
import site.techmoa.worker.rss.trigger.CollectRssUseCase
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit

@Component
class CollectByRssReaderUseCase(
    private val rssClient: RssClient,
    private val articlePort: ArticlePort,
    private val blogPort: BlogPort,
    @Qualifier("rssFetchExecutor") private val fetchExecutor: Executor,
) : CollectRssUseCase {

    private val log = LoggerFactory.getLogger(this.javaClass)

    private val operations = LoggerFactory.getLogger("techmoa.operations")

    private data class FetchResult(
        val blogId: Long,
        val blogName: String,
        val articles: List<Article>,
        val error: Throwable?
    )

    override fun execute() {
        // 1. 구독 블로그 조회
        log.info("Select Blogs List")
        val blogs = blogPort.findAllBy(BlogStatus.ACTIVE)
        if (blogs.isEmpty()) return

        // 2. 각 블로그 RSS에서 아티클 수집
        log.info("Start Collecting Articles from RSS")
        val results = fetchAllFeeds(blogs)

        val articles = results
            .filter { it.error == null }
            .flatMap { it.articles }

        val failedCount = results.count { it.error != null }
        if (failedCount > 0) {
            log.warn(
                "RSS collect partial failure. total=${results.size}, failed=$failedCount",
            )
        }

        // 3. 아티클 저장
        log.info("Save New Articles")
        articlePort.saveAllIgnoringDuplicates(articles)
    }

    private fun fetchAllFeeds(blogs: List<Blog>): List<FetchResult> {
        val total = blogs.size

        val futures = blogs.map { blog ->
            CompletableFuture.supplyAsync(
                { fetchSingleBlog(blog) },
                fetchExecutor
            )
        }

        CompletableFuture.allOf(*futures.toTypedArray()).join()

        val results = futures.map { it.join() }
        val failed = results.count { it.error != null }
        operations.info("RSS collect finished. total={}, success={}, failed={}", total, total - failed, failed)

        return results
    }

    private fun fetchSingleBlog(
        blog: Blog,
    ): FetchResult {
        val started = System.nanoTime()
        val result = try {
            FetchResult(
                blogId = blog.id,
                blogName = blog.name,
                articles = rssClient.fetch(blog),
                error = null,
            )
        } catch (ex: Throwable) {
            log.warn(
                "Failed to collect RSS. blogId=${blog.id}, blogName=${blog.name}",
                ex,
            )
            FetchResult(
                blogId = blog.id,
                blogName = blog.name,
                articles = emptyList(),
                error = ex,
            )
        }
        val durationMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - started)
        val outcome = if (result.error == null) "success" else "failure"
        operations.info("rss_fetch outcome={} duration_ms={} blog_id={} blog_name={}",
            outcome, durationMs, blog.id, blog.name)
        return result
    }
}
