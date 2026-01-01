package site.techmoa.app.batch.rss.fixture

import site.techmoa.app.batch.rss.collector.rssreader.handler.RssCollectContext
import site.techmoa.app.batch.rss.collector.rssreader.handler.RssCollectHandler
import site.techmoa.app.batch.rss.collector.rssreader.handler.RssCollectHandlerChain

class HandlerFixture {
    companion object {
        fun giveMeHandler(order: Int = 0): RssCollectHandler {
            return object : RssCollectHandler {
                override fun getOrder(): Int {
                    return order
                }
                override fun handle(context: RssCollectContext, chain: RssCollectHandlerChain) {
                    chain.next(context)
                }
            }
        }
    }
}
