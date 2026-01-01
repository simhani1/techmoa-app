package site.techmoa.app.batch.rss.collector.rssreader.handler.chain

import org.springframework.stereotype.Component
import site.techmoa.app.batch.rss.collector.rssreader.handler.AbstractRssCollectHandler
import site.techmoa.app.batch.rss.collector.rssreader.handler.RssCollectContext
import site.techmoa.app.storage.db.entity.BlogStatus
import site.techmoa.app.storage.db.repository.BlogRepository

@Component
class LoadActiveBlogsHandler(
    private val blogRepository: BlogRepository,
) : AbstractRssCollectHandler() {

    override fun getOrder(): Int {
        return 1
    }

    override fun handle(context: RssCollectContext) {
        context.blogs = blogRepository.findAllStatus(status = BlogStatus.ACTIVE)
        handleNext(context)
    }
}
