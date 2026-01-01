package site.techmoa.app.batch.rss.collector.rssreader.handler.chain

import com.apptasticsoftware.rssreader.RssReader
import org.springframework.stereotype.Component
import site.techmoa.app.batch.rss.collector.rssreader.handler.RssCollectContext
import site.techmoa.app.batch.rss.collector.rssreader.handler.RssCollectHandler
import site.techmoa.app.batch.rss.collector.rssreader.handler.RssCollectHandlerChain

@Component
class CollectItemsByBlogHandler : RssCollectHandler {

    override fun getOrder(): Int {
        return 2
    }

    override fun handle(context: RssCollectContext, chain: RssCollectHandlerChain) {
        if (context.blogs.isEmpty()) {
            return
        }

        val rssReader = RssReader()
        context.collectedItems = context.blogs.associateWith { blog ->
            rssReader.read(blog.rssLink).toList()
        }
        chain.next(context)
    }
}
