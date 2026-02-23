package site.techmoa.domain.event

import site.techmoa.domain.model.Article
import site.techmoa.domain.model.Webhook

data class NewArticlesEvents(
    val events: List<NewArticlesEvent>
) {
    data class NewArticlesEvent(
        val article: Article,
        val webhook: Webhook,
        val idempotencyKey: String,
        val tag: EventTag = EventTag.NEW_ARTICLE
    )
}
