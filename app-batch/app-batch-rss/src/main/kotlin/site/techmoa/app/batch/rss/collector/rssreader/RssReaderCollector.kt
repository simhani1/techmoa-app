package site.techmoa.app.batch.rss.collector.rssreader

import org.springframework.stereotype.Component
import site.techmoa.app.batch.rss.collector.RssCollector
import site.techmoa.app.batch.rss.collector.rssreader.handler.RssCollectContext
import site.techmoa.app.batch.rss.collector.rssreader.handler.RssCollectHandler
import site.techmoa.app.batch.rss.collector.rssreader.handler.RssCollectHandlerChain

@Component
class RssReaderCollector(
    handlers: List<RssCollectHandler>,
) : RssCollector {

    private val orderedHandlers = handlers.sortedBy { it.getOrder() }

    override fun execute() {
        val context = RssCollectContext()
        val chain = RssCollectHandlerChain(orderedHandlers)
        chain.next(context)
    }
}
