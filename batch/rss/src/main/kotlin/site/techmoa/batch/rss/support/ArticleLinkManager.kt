package site.techmoa.batch.rss.support

import org.springframework.stereotype.Component

@Component
class ArticleLinkManager {

    companion object {
        private const val HTTPS: String = "https"
        private const val HTTP: String = "http"
    }

    fun sanitizeLink(blogLink: String, articleLink: String): String {
        if (articleLink.startsWith(HTTPS)) {
            return articleLink
        }
        if (articleLink.startsWith(HTTP)) {
            return articleLink
        }
        return blogLink + articleLink
    }

}