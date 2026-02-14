package site.techmoa.app.batch.rss.scheduler

import org.springframework.context.annotation.Profile
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import site.techmoa.app.batch.rss.CollectorTemplate

@Component
@Profile("prod")
class ProdCollectorScheduler(
    private val collector: CollectorTemplate
) {
    companion object {
        const val EVERY_30_MINUTES = "0 */30 * * * *"
    }

    @Scheduled(cron = EVERY_30_MINUTES)
    fun run() {
        collector.execute()
    }
}
