package site.techmoa.app.batch.rss.collector.rssreader

import io.kotest.core.spec.style.DescribeSpec
import io.mockk.spyk
import io.mockk.verify
import site.techmoa.app.batch.rss.collector.rssreader.handler.RssCollectContext
import site.techmoa.app.batch.rss.collector.rssreader.handler.RssCollectHandlerChain
import site.techmoa.app.batch.rss.fixture.HandlerFixture

class RssReaderCollectorTest : DescribeSpec({

    describe("RssReaderCollector는") {
        it("getOrder 기준으로 핸들러 순서를 정렬해 체인을 실행한다") {
            val handler3 = spyk(HandlerFixture.giveMeHandler(3))
            val handler2 = spyk(HandlerFixture.giveMeHandler(2))
            val handler1 = spyk(HandlerFixture.giveMeHandler(1))

            val handlers = listOf(
                handler3,
                handler2,
                handler1,
            )

            val collector = RssReaderCollector(handlers)
            collector.execute()

            verify(exactly = 1) { handler1.handle(any<RssCollectContext>(), any<RssCollectHandlerChain>()) }
            verify(exactly = 1) { handler2.handle(any<RssCollectContext>(), any<RssCollectHandlerChain>()) }
            verify(exactly = 1) { handler3.handle(any<RssCollectContext>(), any<RssCollectHandlerChain>()) }
        }
    }
})
