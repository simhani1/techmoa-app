package site.techmoa.batch.rss.trigger

import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Profile
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import site.techmoa.batch.rss.domain.exception.RssCollectionExecutionException

@Component
class RssCollectorTrigger(
    private val collector: CollectRssUseCase
) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    companion object {
        const val EVERY_2_MINUTES = "0 */2 * * * *"
        const val EVERY_30_MINUTES = "0 */30 * * * *"
    }

    @Profile("local")
    @Scheduled(cron = EVERY_2_MINUTES)
    fun runOnLocalProfile() {
        runWithDuration()
    }

    @Profile("prod")
    @Scheduled(cron = EVERY_30_MINUTES)
    fun runOnProdProfile() {
        runWithDuration()
    }

    private fun runWithDuration() {
        val startAt = System.currentTimeMillis()
        log.info("[${this.javaClass.simpleName}] Starting collection")

        try {
            collector.execute()
            val elapsedMs = System.currentTimeMillis() - startAt
            log.info(
                "[${this.javaClass.simpleName}] Finished collection, elapsedMs=$elapsedMs",
            )
        } catch (ex: Throwable) {
            val elapsedMs = System.currentTimeMillis() - startAt
            log.error(
                "[${this.javaClass.simpleName}] Failed collection, elapsedMs=$elapsedMs",
                ex
            )
            throw RssCollectionExecutionException(
                "RSS 수집 작업 실행 실패: elapsedMs=$elapsedMs",
                ex
            )
        }
    }
}
