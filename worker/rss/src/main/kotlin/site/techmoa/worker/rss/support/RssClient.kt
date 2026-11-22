package site.techmoa.worker.rss.support

import site.techmoa.worker.rss.domain.model.Article
import site.techmoa.worker.rss.domain.model.Blog

interface RssClient {
    fun fetch(blog: Blog): List<Article>
}