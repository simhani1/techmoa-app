package site.techmoa.batch.schedules.trigger.prod

import org.springframework.context.annotation.Profile
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import site.techmoa.batch.schedules.service.OutboxDispatchService
import site.techmoa.batch.schedules.trigger.support.OutboxDispatchMessageTriggerSupport

@Component
@Profile("prod")
class ProdPublishMessageTrigger(
    outboxDispatchService: OutboxDispatchService
) : OutboxDispatchMessageTriggerSupport(outboxDispatchService) {

    companion object {
        const val EVEY_2_MINUTES = "0 0/2 * * * *"
    }

    @Scheduled(cron = EVEY_2_MINUTES, scheduler = "schedulesTaskScheduler")
    fun run() {
        runWithDuration()
    }
}
