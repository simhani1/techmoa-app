package site.techmoa.app.batch.rss.collector.rssreader.handler.chain

import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import site.techmoa.app.batch.rss.collector.rssreader.handler.RssCollectContext
import site.techmoa.app.batch.rss.collector.rssreader.handler.RssCollectHandler
import site.techmoa.app.batch.rss.collector.rssreader.handler.RssCollectHandlerChain
import site.techmoa.app.storage.db.entity.BlogStatus
import site.techmoa.app.storage.db.repository.BlogRepository

@Component
class LoadActiveBlogsHandler(
    private val blogRepository: BlogRepository,
) : RssCollectHandler {

    override fun getOrder(): Int {
        return 1
    }

    @Transactional(readOnly = true)
    override fun handle(context: RssCollectContext, chain: RssCollectHandlerChain) {
        context.blogs = blogRepository.findAllStatus(status = BlogStatus.ACTIVE)
        chain.next(context)
    }
}
