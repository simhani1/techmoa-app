package site.techmoa.worker.scheduler.trigger.prod

import org.springframework.context.annotation.Profile
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import site.techmoa.worker.scheduler.service.ArticleNotificationProducer
import site.techmoa.worker.scheduler.trigger.support.NotificationProducerTriggerSupport

@Component
@Profile("prod")
class ProdNotificationProducerTrigger(
    producer: ArticleNotificationProducer
) : NotificationProducerTriggerSupport(producer) {

    companion object {
        const val MINUTE_5_AND_35 = "0 5,35 * * * *"
    }

    @Scheduled(cron = MINUTE_5_AND_35, scheduler = "schedulesTaskScheduler")
    fun run() {
        runWithDuration()
    }
}
