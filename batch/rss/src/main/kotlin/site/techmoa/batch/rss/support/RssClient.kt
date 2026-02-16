package site.techmoa.batch.rss.support

import site.techmoa.batch.rss.domain.model.Article
import site.techmoa.batch.rss.domain.model.Blog

interface RssClient {
    fun fetch(blog: Blog): List<Article>
}