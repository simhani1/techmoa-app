package site.techmoa.batch.schedules.event

import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener
import site.techmoa.batch.schedules.annotation.EventHandler
import site.techmoa.batch.schedules.repository.OutboxRepository

@EventHandler
class NewArticlesEventHandler(
    private val outboxRepository: OutboxRepository
) {

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    fun recordMessage(event: NewArticlesEvents) {
        outboxRepository.save(event)
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun publishMessage(event: NewArticlesEvents) {

    }
}