package site.techmoa.app.batch.rss.collector.rssreader.handler.chain

import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import site.techmoa.app.batch.rss.collector.rssreader.handler.RssCollectContext
import site.techmoa.app.batch.rss.collector.rssreader.handler.RssCollectHandler
import site.techmoa.app.batch.rss.collector.rssreader.handler.RssCollectHandlerChain
import site.techmoa.app.storage.db.repository.ArticleRepository

@Component
class SaveNewArticlesHandler(
    private val articleRepository: ArticleRepository,
) : RssCollectHandler {

    override fun getOrder(): Int {
        return 100
    }

    @Transactional
    override fun handle(context: RssCollectContext, chain: RssCollectHandlerChain) {
        val newArticles = context.newArticles
        if (newArticles.isNotEmpty()) {
            articleRepository.saveAll(newArticles)
        }
        chain.next(context)
    }
}
