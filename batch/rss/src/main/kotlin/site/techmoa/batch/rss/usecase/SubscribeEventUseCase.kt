package site.techmoa.batch.rss.usecase

import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener
import site.techmoa.batch.rss.domain.event.NewArticlesCollectedEvent
import site.techmoa.batch.rss.port.ArticleCreatedOutboxPort

@Component
class SubscribeEventUseCase(
    private val outboxPort: ArticleCreatedOutboxPort
) {

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    fun on(event: NewArticlesCollectedEvent) {
        outboxPort.saveAll(event.events)
    }
}
