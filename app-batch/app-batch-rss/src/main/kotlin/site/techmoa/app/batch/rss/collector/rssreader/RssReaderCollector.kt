package site.techmoa.app.batch.rss.collector.rssreader

import org.springframework.stereotype.Component
import site.techmoa.app.batch.rss.collector.RssCollector
import site.techmoa.app.batch.rss.collector.rssreader.handler.RssCollectContext
import site.techmoa.app.batch.rss.collector.rssreader.handler.chain.CollectItemsByBlogHandler
import site.techmoa.app.batch.rss.collector.rssreader.handler.chain.LoadActiveBlogsHandler
import site.techmoa.app.batch.rss.collector.rssreader.handler.chain.SaveUniqueArticlesHandler

@Component
class RssReaderCollector(
    private val loadActiveBlogsHandler: LoadActiveBlogsHandler,
    private val collectItemsByBlogHandler: CollectItemsByBlogHandler,
    private val saveUniqueArticlesHandler: SaveUniqueArticlesHandler,
) : RssCollector {

    init {
        loadActiveBlogsHandler
            .setNext(collectItemsByBlogHandler)
            .setNext(saveUniqueArticlesHandler)
    }

    override fun execute() {
        val context = RssCollectContext()
        loadActiveBlogsHandler.handle(context)
    }
}