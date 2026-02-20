package site.techmoa.batch.rss.port

import site.techmoa.batch.rss.domain.event.NewArticlesCollectedEvent.ArticleCreatedOutboxEvent

interface ArticleCreatedOutboxPort {
    fun saveAll(events: List<ArticleCreatedOutboxEvent>)
}
