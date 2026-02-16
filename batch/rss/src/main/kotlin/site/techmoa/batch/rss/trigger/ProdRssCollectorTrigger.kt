package site.techmoa.batch.rss.trigger

import org.springframework.context.annotation.Profile
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
@Profile("prod")
class ProdRssCollectorTrigger(
    collector: CollectRssUseCase
) : RssCollectorTriggerSupport(collector) {

    companion object {
        const val EVERY_30_MINUTES = "0 */30 * * * *"
    }

    @Scheduled(cron = EVERY_30_MINUTES)
    fun run() {
        runWithDuration()
    }
}
