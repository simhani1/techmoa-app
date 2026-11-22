package site.techmoa.worker.scheduler.trigger

import org.springframework.context.annotation.Profile
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import site.techmoa.worker.scheduler.service.OutboxDispatchService

@Component
@Profile("prod", "local")
class PublishMessageTrigger(private val dispatcher: OutboxDispatchService) {
    @Scheduled(fixedDelay = 1000, scheduler = "schedulesTaskScheduler")
    fun run() = dispatcher.dispatchPending()
}
