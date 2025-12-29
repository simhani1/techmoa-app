package site.techmoa.app.batch.rss

import com.apptasticsoftware.rssreader.Item
import com.apptasticsoftware.rssreader.RssReader
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import site.techmoa.app.storage.db.entity.Article
import site.techmoa.app.storage.db.entity.Blog
import site.techmoa.app.storage.db.entity.BlogStatus
import site.techmoa.app.storage.db.repository.ArticleRepository
import site.techmoa.app.storage.db.repository.BlogRepository
import java.time.LocalDateTime

@Component
class RssCollector(
    val blogRepository: BlogRepository,
    val articleRepository: ArticleRepository,
) {

    companion object {
        const val EVERY_30_MINUTES = "0 1/30 * * * *"
    }

    @Scheduled(cron = EVERY_30_MINUTES)
    @Transactional
    fun saveNewArticles() {
        // 1. 블로그마다 rssLink에서 아티클을 수집한다
        val blogs = loadActiveBlogs()
        val collectedItems = collectItemsByBlog(blogs)
        // 2. 수집된 블로그의 아티클마다 새로운 아티클을 db에 잭재한다
        saveUniqueArticles(collectedItems)
    }

    private fun loadActiveBlogs(): List<Blog> {
        return blogRepository.findAllStatus(status = BlogStatus.ACTIVE)
    }

    private fun collectItemsByBlog(blogs: List<Blog>): Map<Blog, List<Item>> {
        val rssReader = RssReader()
        return blogs.associateWith { blog ->
            rssReader.read(blog.rssLink).toList()
        }
    }

    private fun saveUniqueArticles(collectedItems: Map<Blog, List<Item>>) {
        for ((blog, items) in collectedItems) {
            val newArticles = items.mapNotNull { item -> toArticle(blog, item) }
                .filter { article -> isNewArticle(article) }
            if (newArticles.isNotEmpty()) {
                articleRepository.saveAll(newArticles)
            }
        }
    }

    private fun toArticle(blog: Blog, item: Item): Article? {
        val blogId = blog.id ?: return null
        val title = item.title.orElse(null) ?: return null
        val link = item.link.orElse(null) ?: return null
        val guid = item.guid.orElse(null) ?: link
        val publishedAt = extractPublishedAt(item) ?: return null
        return Article.of(
            blogId = blogId,
            title = title,
            link = link,
            guid = guid,
            pubDate = publishedAt
        )
    }

    private fun extractPublishedAt(item: Item): LocalDateTime? {
        val pubDate = item.pubDateAsZonedDateTime.orElse(null)
            ?: item.updatedAsZonedDateTime.orElse(null)
            ?: return null
        return pubDate.toLocalDateTime()
    }

    private fun isNewArticle(article: Article): Boolean {
        return !articleRepository.existsByBlogIdAndGuid(article.blogId, article.guid)
    }
}
