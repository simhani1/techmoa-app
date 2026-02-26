package site.techmoa.batch.schedules.event

import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener
import site.techmoa.batch.schedules.annotation.EventHandler
import site.techmoa.batch.schedules.repository.OutboxRepository
import site.techmoa.domain.event.OutboxMessages

@EventHandler
class NewArticlesEventHandler(
    private val outboxRepository: OutboxRepository
) {

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    @Transactional(propagation = Propagation.MANDATORY)
    fun recordMessage(messages: OutboxMessages) {
        outboxRepository.save(messages)
    }
}