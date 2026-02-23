package site.techmoa.batch.schedules.trigger.local

import org.springframework.context.annotation.Profile
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import site.techmoa.batch.schedules.service.ScanNewArticlesService
import site.techmoa.batch.schedules.trigger.support.PublishNotificationMessageTriggerSupport

@Component
@Profile("local")
class LocalPublishNotificationMessageTrigger(
    service: ScanNewArticlesService
) : PublishNotificationMessageTriggerSupport(service) {

    companion object {
        const val EVERY_2_MINUTES = "0 */2 * * * *"
    }

    @Scheduled(cron = EVERY_2_MINUTES, scheduler = "schedulesTaskScheduler")
    fun run() {
        runWithDuration()
    }
}
