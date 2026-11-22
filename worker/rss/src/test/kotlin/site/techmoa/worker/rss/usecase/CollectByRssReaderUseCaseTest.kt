package site.techmoa.worker.rss.usecase

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import site.techmoa.worker.rss.config.BatchRssConfig
import site.techmoa.worker.rss.domain.model.Article
import site.techmoa.worker.rss.domain.model.Blog
import site.techmoa.worker.rss.domain.model.BlogStatus
import site.techmoa.worker.rss.port.ArticlePort
import site.techmoa.worker.rss.port.BlogPort
import site.techmoa.worker.rss.support.RssClient
import java.io.IOException
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class CollectByRssReaderUseCaseTest : BehaviorSpec({
    Given("활성 피드 5개 중 한 곳이 실패하고 한 곳은 글이 없을 때") {
        Then("모든 수집을 먼저 시작하고 나머지 세 곳의 글을 한 번에 저장한다") {
            val blogs = (1L..5L).map { Blog(it, "", "blog$it", "", "") }
            val started = CountDownLatch(blogs.size)
            val blogPort = mockk<BlogPort>()
            val articlePort = mockk<ArticlePort>(relaxUnitFun = true)
            every { blogPort.findAllBy(BlogStatus.ACTIVE) } returns blogs
            fun article(id: Long) = Article.of(blogId = id, title = "title", link = "/$id", guid = "$id", pubDate = 0)
            val rssClient = object : RssClient {
                override fun fetch(blog: Blog): List<Article> {
                    Thread.currentThread().isVirtual shouldBe true
                    started.countDown()
                    started.await(5, TimeUnit.SECONDS) shouldBe true
                    return when (blog.id) {
                        1L -> throw IOException("unavailable")
                        2L -> emptyList()
                        else -> listOf(article(blog.id))
                    }
                }
            }
            BatchRssConfig().rssFetchExecutor().use { executor ->
                CollectByRssReaderUseCase(rssClient, articlePort, blogPort, executor).execute()
            }
            verify(exactly = 1) {
                articlePort.saveAllIgnoringDuplicates(listOf(article(3), article(4), article(5)))
            }
        }
    }
})
