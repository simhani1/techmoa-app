package site.techmoa.batch.rss.usecase

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import site.techmoa.batch.rss.domain.model.Article
import site.techmoa.batch.rss.domain.model.Blog
import site.techmoa.batch.rss.domain.model.BlogStatus
import site.techmoa.batch.rss.port.ArticlePort
import site.techmoa.batch.rss.port.BlogPort
import site.techmoa.batch.rss.support.RssClient
import site.techmoa.batch.rss.trigger.CollectRssUseCase
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor
import java.util.concurrent.atomic.AtomicInteger

@Component
class CollectByRssReaderUseCase(
    private val rssClient: RssClient,
    private val articlePort: ArticlePort,
    private val blogPort: BlogPort,
    @Qualifier("rssFetchExecutor") private val fetchExecutor: Executor,
) : CollectRssUseCase {

    private val log = LoggerFactory.getLogger(this.javaClass)

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
        val results = fetchWithLimitedConcurrency(blogs)

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

    private fun fetchWithLimitedConcurrency(blogs: List<Blog>): List<FetchResult> {
        val total = blogs.size
        val failures = AtomicInteger(0)

        val futures = blogs.map { blog ->
            CompletableFuture.supplyAsync(
                { fetchSingleBlog(blog, failures) },
                fetchExecutor
            )
        }

        CompletableFuture.allOf(*futures.toTypedArray()).join()

        val results = futures.map { it.join() }
        log.info(
            "RSS collect finished. total=$total, success=${total - failures.get()}, failed=${failures.get()}",
        )

        return results
    }

    private fun fetchSingleBlog(
        blog: Blog,
        failures: AtomicInteger,
    ): FetchResult {
        return try {
            FetchResult(
                blogId = blog.id,
                blogName = blog.name,
                articles = rssClient.fetch(blog),
                error = null,
            )
        } catch (ex: Throwable) {
            failures.incrementAndGet()
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
    }
}
