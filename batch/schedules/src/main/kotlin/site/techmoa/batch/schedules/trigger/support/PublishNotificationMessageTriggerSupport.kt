package site.techmoa.batch.schedules.trigger

import org.slf4j.LoggerFactory
import site.techmoa.batch.schedules.service.ScanNewArticlesService

abstract class PublishNotificationMessageTriggerSupport(
    private val service: ScanNewArticlesService
) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    protected fun runWithDuration() {
        val startAt = System.currentTimeMillis()
        log.info("[{}] Starting notification publish scheduler", this.javaClass.simpleName)

        try {
            service.execute()
            val elapsedMs = System.currentTimeMillis() - startAt
            log.info("[{}] Finished scheduler, elapsedMs={}", this.javaClass.simpleName, elapsedMs)
        } catch (ex: Throwable) {
            val elapsedMs = System.currentTimeMillis() - startAt
            log.error("[{}] Failed scheduler, elapsedMs={}", this.javaClass.simpleName, elapsedMs, ex)
            throw ex
        }
    }
}
