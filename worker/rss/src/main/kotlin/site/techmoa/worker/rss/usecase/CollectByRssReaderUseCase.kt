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
import org.springframework.core.task.AsyncTaskExecutor
import java.util.concurrent.Callable
import java.util.concurrent.TimeUnit

@Component
class CollectByRssReaderUseCase(
    private val rssClient: RssClient,
    private val articlePort: ArticlePort,
    private val blogPort: BlogPort,
    @Qualifier("rssFetchExecutor") private val fetchExecutor: AsyncTaskExecutor,
) : CollectRssUseCase {

    private val log = LoggerFactory.getLogger(this.javaClass)

    private val operations = LoggerFactory.getLogger("techmoa.operations")

    private data class FetchResult(val articles: List<Article>, val successful: Boolean)

    override fun execute() {
        val blogs = blogPort.findAllBy(BlogStatus.ACTIVE)
        if (blogs.isEmpty()) return

        val results = fetchAllFeeds(blogs)
        val successful = results.count { it.successful }
        operations.info("RSS collect finished. total={}, success={}, failed={}",
            results.size, successful, results.size - successful)

        articlePort.saveAllIgnoringDuplicates(results.flatMap { it.articles })
    }

    private fun fetchAllFeeds(blogs: List<Blog>): List<FetchResult> {
        val tasks = blogs.map { blog -> fetchExecutor.submit(Callable { fetchSingleBlog(blog) }) }
        return try {
            tasks.map { it.get() }
        } catch (ex: InterruptedException) {
            tasks.forEach { it.cancel(true) }
            Thread.currentThread().interrupt()
            throw ex
        }
    }

    private fun fetchSingleBlog(blog: Blog): FetchResult {
        val started = System.nanoTime()
        val result = try {
            FetchResult(rssClient.fetch(blog), successful = true)
        } catch (ex: InterruptedException) {
            Thread.currentThread().interrupt()
            throw ex
        } catch (ex: Exception) {
            log.warn("Failed to collect RSS. blogId=${blog.id}, blogName=${blog.name}", ex)
            FetchResult(emptyList(), successful = false)
        }

        val durationMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - started)
        val outcome = if (result.successful) "success" else "failure"
        operations.info("rss_fetch outcome={} duration_ms={} blog_id={} blog_name={}",
            outcome, durationMs, blog.id, blog.name)
        return result
    }
}
