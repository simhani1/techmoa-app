package site.techmoa.worker.scheduler.trigger.local

import org.springframework.context.annotation.Profile
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import site.techmoa.worker.scheduler.service.OutboxDispatchService
import site.techmoa.worker.scheduler.trigger.support.OutboxDispatchMessageTriggerSupport

@Component
@Profile("local")
class LocalPublishMessageTrigger(
    outboxDispatchService: OutboxDispatchService
) : OutboxDispatchMessageTriggerSupport(outboxDispatchService) {

    companion object {
        const val EVERY_30_SECONDS = "0 0/1 * * * *"
    }

    @Scheduled(cron = EVERY_30_SECONDS, scheduler = "schedulesTaskScheduler")
    fun run() {
        runWithDuration()
    }
}
