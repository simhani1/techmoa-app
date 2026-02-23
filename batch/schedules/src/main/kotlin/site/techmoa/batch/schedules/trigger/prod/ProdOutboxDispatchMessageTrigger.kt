package site.techmoa.batch.schedules.trigger.prod

import org.springframework.context.annotation.Profile
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import site.techmoa.batch.schedules.service.OutboxDispatchService
import site.techmoa.batch.schedules.trigger.support.OutboxDispatchMessageTriggerSupport

@Component
@Profile("prod")
class ProdOutboxDispatchMessageTrigger(
    outboxDispatchService: OutboxDispatchService
) : OutboxDispatchMessageTriggerSupport(outboxDispatchService) {

    companion object {
        const val EVERY_HOUR = "0 0 0/1 * * *"
    }

    @Scheduled(cron = EVERY_HOUR, scheduler = "schedulesTaskScheduler")
    fun run() {
        runWithDuration()
    }
}
