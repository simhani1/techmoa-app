package site.techmoa.worker.scheduler.trigger.local

import org.springframework.context.annotation.Profile
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import site.techmoa.worker.scheduler.service.ArticleNotificationProducer
import site.techmoa.worker.scheduler.trigger.support.NotificationProducerTriggerSupport

@Component
@Profile("local")
class LocalNotificationProducerTrigger(
    producer: ArticleNotificationProducer
) : NotificationProducerTriggerSupport(producer) {

    companion object {
        const val EVERY_2_MINUTES = "0 */2 * * * *"
    }

    @Scheduled(cron = EVERY_2_MINUTES, scheduler = "schedulesTaskScheduler")
    fun run() {
        runWithDuration()
    }
}
