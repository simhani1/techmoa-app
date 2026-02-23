package site.techmoa.batch.schedules.trigger

import org.springframework.context.annotation.Profile
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import site.techmoa.batch.schedules.service.ScanNewArticlesService

@Component
@Profile("prod")
class ProdPublishNotificationMessageTrigger(
    service: ScanNewArticlesService
) : PublishNotificationMessageTriggerSupport(service) {

    companion object {
        const val MINUTE_5_AND_35 = "0 5,35 * * * *"
    }

    @Scheduled(cron = MINUTE_5_AND_35, scheduler = "schedulesTaskScheduler")
    fun run() {
        runWithDuration()
    }
}
