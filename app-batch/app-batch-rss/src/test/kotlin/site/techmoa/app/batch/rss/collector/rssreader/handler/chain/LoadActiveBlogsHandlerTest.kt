package site.techmoa.app.batch.rss.collector.rssreader.handler.chain

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import site.techmoa.app.batch.rss.collector.rssreader.handler.RssCollectContext
import site.techmoa.app.batch.rss.collector.rssreader.handler.RssCollectHandlerChain
import site.techmoa.app.batch.rss.fixture.HandlerFixture
import site.techmoa.app.storage.db.entity.BlogEntity
import site.techmoa.app.storage.db.entity.BlogStatus
import site.techmoa.app.storage.db.repository.BlogRepository

class LoadActiveBlogsHandlerTest : DescribeSpec({

    lateinit var handler: LoadActiveBlogsHandler
    lateinit var blogRepository: BlogRepository

    beforeTest {
        blogRepository = mockk<BlogRepository>(relaxed = true)
        handler = LoadActiveBlogsHandler(blogRepository)
    }

    context("LoadActiveBlogsHandler는") {
        it("1번째로 동작한다") {
            handler.getOrder().shouldBe(1)
        }
        describe("ACTIVE 상태의 블로그가 조회되면") {
            it("다음 핸들러를 호출한다") {
                val context = RssCollectContext()
                val blog = BlogEntity.of("link", "name", "logo", "rss")
                every { blogRepository.findAllStatus(BlogStatus.ACTIVE) } returns listOf(blog)

                val nextHandler = spyk(HandlerFixture.giveMeHandler())
                val chain = RssCollectHandlerChain(listOf(nextHandler))

                handler.handle(context, chain)

                context.blogs.shouldBe(listOf(blog))
                verify(exactly = 1) { nextHandler.handle(any<RssCollectContext>(), any<RssCollectHandlerChain>()) }
            }
        }
        describe("ACTIVE 상태의 블로그가 조회하지 않아도") {
            it("다음 핸들러를 호출한다") {
                val context = RssCollectContext()
                val blog = BlogEntity(1L, "link", "name", "logo", "rss", BlogStatus.ACTIVE)
                every { blogRepository.findAllStatus(BlogStatus.ACTIVE) } returns listOf(blog)

                val nextHandler = spyk(HandlerFixture.giveMeHandler())
                val chain = RssCollectHandlerChain(listOf(nextHandler))

                handler.handle(context, chain)

                context.blogs.shouldBe(listOf(blog))
                verify(exactly = 1) { nextHandler.handle(any<RssCollectContext>(), any<RssCollectHandlerChain>()) }
            }
        }
    }

})
