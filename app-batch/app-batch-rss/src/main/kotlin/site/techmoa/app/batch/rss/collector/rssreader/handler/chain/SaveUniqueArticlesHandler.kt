package site.techmoa.app.batch.rss.collector.rssreader.handler.chain

import com.apptasticsoftware.rssreader.Item
import org.springframework.stereotype.Component
import site.techmoa.app.batch.rss.collector.rssreader.handler.AbstractRssCollectHandler
import site.techmoa.app.batch.rss.collector.rssreader.handler.RssCollectContext
import site.techmoa.app.storage.db.entity.ArticleEntity
import site.techmoa.app.storage.db.entity.BlogEntity
import site.techmoa.app.storage.db.repository.ArticleRepository

@Component
class SaveUniqueArticlesHandler(
    private val articleRepository: ArticleRepository,
) : AbstractRssCollectHandler() {

    override fun getOrder(): Int {
        return 10
    }

    override fun handle(context: RssCollectContext) {
        for ((blog, items) in context.collectedItems) {
            saveNewArticles(filterNewArticles(items, blog))
        }
        handleNext(context)
    }

    private fun saveNewArticles(newArticles: List<ArticleEntity>) {
        if (newArticles.isNotEmpty()) {
            articleRepository.saveAll(newArticles)
        }
    }

    private fun filterNewArticles(
        items: List<Item>,
        blog: BlogEntity
    ): List<ArticleEntity> = items.mapNotNull { item -> toArticle(blog, item) }
        .filter { article -> isNewArticle(article) }

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
