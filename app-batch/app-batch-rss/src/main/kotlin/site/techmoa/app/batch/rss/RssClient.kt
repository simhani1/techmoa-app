package site.techmoa.app.batch.rss

import site.techmoa.app.core.article.Article
import site.techmoa.app.core.blog.Blog

interface RssClient {
    fun fetch(blog: Blog): List<Article>
}