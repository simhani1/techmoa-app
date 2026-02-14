package site.techmoa.app.batch.rss

import site.techmoa.app.common.article.Article
import site.techmoa.app.common.blog.Blog

interface RssClient {
    fun fetch(blog: site.techmoa.app.common.blog.Blog): List<site.techmoa.app.common.article.Article>
}