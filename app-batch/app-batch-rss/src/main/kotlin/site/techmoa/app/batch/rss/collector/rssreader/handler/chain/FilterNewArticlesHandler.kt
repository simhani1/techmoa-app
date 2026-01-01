package site.techmoa.app.batch.rss.collector.rssreader.handler.chain

import com.apptasticsoftware.rssreader.Item
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import site.techmoa.app.batch.rss.collector.rssreader.handler.RssCollectContext
import site.techmoa.app.batch.rss.collector.rssreader.handler.RssCollectHandler
import site.techmoa.app.batch.rss.collector.rssreader.handler.RssCollectHandlerChain
import site.techmoa.app.storage.db.entity.ArticleEntity
import site.techmoa.app.storage.db.entity.BlogEntity
import site.techmoa.app.storage.db.repository.ArticleRepository

@Component
class FilterNewArticlesHandler(
    private val articleRepository: ArticleRepository,
) : RssCollectHandler {

    override fun getOrder(): Int {
        return 3
    }

    @Transactional(readOnly = true)
    override fun handle(context: RssCollectContext, chain: RssCollectHandlerChain) {
        val newArticles = mutableListOf<ArticleEntity>()
        for ((blog, items) in context.collectedItems) {
            newArticles.addAll(filterNewArticles(items, blog))
        }
        context.newArticles = newArticles
        chain.next(context)
    }

    private fun filterNewArticles(items: List<Item>, blog: BlogEntity): List<ArticleEntity> {
        val filtered = items.mapNotNull { item -> toArticle(blog, item) }
            .filter { article -> isNewArticle(article) }
        return filtered
    }

    private fun toArticle(blog: BlogEntity, item: Item): ArticleEntity? {
        val blogId = blog.id
        val title = item.title.orElse(null) ?: return null
        val link = item.link.orElse(null) ?: return null
        val guid = item.guid.orElse(null) ?: link
        val pubDate = parseToEpochMillis(item) ?: return null
        return ArticleEntity.Companion.of(
            blogId = blogId,
            title = title,
            link = link,
            guid = guid,
            pubDate = pubDate
        )
    }

    private fun parseToEpochMillis(item: Item): Long? {
        return item.pubDateAsZonedDateTime.orElse(null)
            ?.toInstant()
            ?.toEpochMilli()
    }

    private fun isNewArticle(articleEntity: ArticleEntity): Boolean {
        return !articleRepository.existsByBlogIdAndGuid(articleEntity.blogId, articleEntity.guid)
    }
}
