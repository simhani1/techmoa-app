package site.techmoa.batch.rss.trigger

import org.slf4j.LoggerFactory
import site.techmoa.batch.rss.domain.exception.RssCollectionExecutionException

abstract class RssCollectorTriggerSupport(
    private val collector: CollectRssUseCase
) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    protected fun runWithDuration() {
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
