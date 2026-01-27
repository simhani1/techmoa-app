package site.techmoa.app.batch.rss

import site.techmoa.app.common.article.domain.Article
import site.techmoa.app.common.blog.Blog

interface RssClient {
    fun fetch(blog: Blog): List<Article>
}