package site.techmoa.app.batch.rss.collector.rssreader.handler.chain

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.spyk
import io.mockk.verify
import site.techmoa.app.batch.rss.collector.rssreader.handler.RssCollectContext
import site.techmoa.app.batch.rss.collector.rssreader.handler.RssCollectHandlerChain
import site.techmoa.app.batch.rss.fixture.HandlerFixture

class CollectItemsByBlogHandlerTest : DescribeSpec({

    lateinit var handler: CollectItemsByBlogHandler

    beforeTest {
        handler = CollectItemsByBlogHandler()
    }

    describe("CollectItemsByBlogHandler는") {
        it("2번째로 동작한다") {
            handler.getOrder().shouldBe(2)
        }
        context("블로그마다 RSS 링크를 호출하고") {
            it("Item 객체로 변환 후 다음 핸들러를 호출한다") {
                val context = RssCollectContext()
                val nextHandler = spyk(HandlerFixture.giveMeHandler())
                val chain = RssCollectHandlerChain(listOf(nextHandler))

                handler.handle(context, chain)

                context.collectedItems.shouldBe(emptyMap())
                verify(exactly = 1) { nextHandler.handle(any<RssCollectContext>(), any<RssCollectHandlerChain>()) }
            }
        }
    }
})
