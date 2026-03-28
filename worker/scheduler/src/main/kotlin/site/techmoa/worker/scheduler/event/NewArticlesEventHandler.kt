package site.techmoa.worker.scheduler.event

import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener
import site.techmoa.domain.event.OutboxMessages

@site.techmoa.worker.scheduler.annotation.EventHandler
class NewArticlesEventHandler(
    private val outboxRepository: site.techmoa.worker.scheduler.repository.OutboxRepository
) {

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    @Transactional(propagation = Propagation.MANDATORY)
    fun recordMessage(messages: OutboxMessages) {
        outboxRepository.save(messages)
    }
}