package site.techmoa.batch.schedules.trigger

import org.springframework.context.annotation.Profile
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import site.techmoa.batch.schedules.usecase.PublishNotificationMessageUseCase

@Component
@Profile("local")
class LocalPublishNotificationMessageTrigger(
    useCase: PublishNotificationMessageUseCase
) : PublishNotificationMessageTriggerSupport(useCase) {

    companion object {
        const val EVERY_2_MINUTES = "0 */2 * * * *"
    }

    @Scheduled(cron = EVERY_2_MINUTES, scheduler = "schedulesTaskScheduler")
    fun run() {
        runWithDuration()
    }
}
