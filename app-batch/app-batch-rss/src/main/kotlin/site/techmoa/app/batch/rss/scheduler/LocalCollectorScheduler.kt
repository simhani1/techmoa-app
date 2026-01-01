package site.techmoa.app.batch.rss.scheduler

import org.springframework.context.annotation.Profile
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import site.techmoa.app.batch.rss.collector.RssCollector

@Component
@Profile("default")
class LocalCollectorScheduler(
    private val collector: RssCollector
) {
    companion object {
        const val EVERY_MINUTE = "0 * * * * *"
    }
    @Scheduled(cron = EVERY_MINUTE)
    fun run() {
        collector.execute()
    }
}