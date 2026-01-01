package site.techmoa.app.batch.rss.collector.rssreader.handler.chain

import com.apptasticsoftware.rssreader.RssReader
import org.springframework.stereotype.Component
import site.techmoa.app.batch.rss.collector.rssreader.handler.AbstractRssCollectHandler
import site.techmoa.app.batch.rss.collector.rssreader.handler.RssCollectContext

@Component
class CollectItemsByBlogHandler : AbstractRssCollectHandler() {

    override fun getOrder(): Int {
        return 2
    }

    override fun handle(context: RssCollectContext) {
        if (context.blogs.isEmpty()) {
            context.collectedItems = emptyMap()
            handleNext(context)
            return
        }

        val rssReader = RssReader()
        context.collectedItems = context.blogs.associateWith { blog ->
            rssReader.read(blog.rssLink).toList()
        }
        handleNext(context)
    }
}
