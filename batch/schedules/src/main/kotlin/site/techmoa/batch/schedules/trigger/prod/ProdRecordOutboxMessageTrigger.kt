package site.techmoa.batch.schedules.trigger.prod

import org.springframework.context.annotation.Profile
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import site.techmoa.batch.schedules.service.ScanNewArticlesService
import site.techmoa.batch.schedules.trigger.support.RecordOutboxMessageTriggerSupport

@Component
@Profile("prod")
class ProdRecordOutboxMessageTrigger(
    service: ScanNewArticlesService
) : RecordOutboxMessageTriggerSupport(service) {

    companion object {
        const val MINUTE_5_AND_35 = "0 5,35 * * * *"
    }

    @Scheduled(cron = MINUTE_5_AND_35, scheduler = "schedulesTaskScheduler")
    fun run() {
        runWithDuration()
    }
}
