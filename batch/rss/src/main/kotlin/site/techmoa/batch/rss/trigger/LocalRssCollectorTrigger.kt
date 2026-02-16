package site.techmoa.batch.rss.trigger

import org.springframework.context.annotation.Profile
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
@Profile("local")
class LocalRssCollectorTrigger(
    collector: CollectRssUseCase
) : RssCollectorTriggerSupport(collector) {

    companion object {
        const val EVERY_2_MINUTES = "0 */2 * * * *"
    }

    @Scheduled(cron = EVERY_2_MINUTES, scheduler = "rssTaskScheduler")
    fun run() {
        runWithDuration()
    }
}
