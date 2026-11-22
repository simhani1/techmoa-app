package site.techmoa.worker.scheduler.trigger.support

import org.slf4j.LoggerFactory
import site.techmoa.worker.scheduler.service.ArticleNotificationProducer

abstract class NotificationProducerTriggerSupport(
    private val producer: ArticleNotificationProducer
) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    protected fun runWithDuration() {
        val startAt = System.currentTimeMillis()
        log.info("[{}] Starting record outbox message scheduler", this.javaClass.simpleName)

        try {
            producer.enqueueNewArticleNotifications()
            val elapsedMs = System.currentTimeMillis() - startAt
            log.info("[{}] Finished scheduler, elapsedMs={}", this.javaClass.simpleName, elapsedMs)
        } catch (ex: Throwable) {
            val elapsedMs = System.currentTimeMillis() - startAt
            log.error("[{}] Failed scheduler, elapsedMs={}", this.javaClass.simpleName, elapsedMs, ex)
            throw ex
        }
    }
}
