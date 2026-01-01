package site.techmoa.app.batch.rss

import org.springframework.context.annotation.Profile
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import site.techmoa.app.batch.rss.collector.RssCollector

@Component
@Profile("prod")
class RssCollectorProdScheduler(
    private val collector: RssCollector
) {
    companion object {
        const val EVERY_30_MINUTES = "0 1/30 * * * *"
    }
    @Scheduled(cron = EVERY_30_MINUTES)
    fun run() {
        collector.execute()
    }
}