package site.techmoa.batch.schedules.trigger.support

import org.slf4j.LoggerFactory
import site.techmoa.batch.schedules.service.OutboxDispatchService

abstract class OutboxDispatchMessageTriggerSupport(
    private val outboxDispatchService: OutboxDispatchService
) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    protected fun runWithDuration() {
        val startAt = System.currentTimeMillis()
        val className = this.javaClass.simpleName
        log.info("[{}] Starting outbox dispatch scheduler", className)

        try {
            outboxDispatchService.dispatchPending()
            val elapsedMs = System.currentTimeMillis() - startAt
            log.info("[{}] Finished outbox dispatch scheduler, elapsedMs={}", className, elapsedMs)
        } catch (ex: Throwable) {
            val elapsedMs = System.currentTimeMillis() - startAt
            log.error(
                "[{}] Failed outbox dispatch scheduler, elapsedMs={}",
                className,
                elapsedMs,
                ex
            )
            throw ex
        }
    }
}
