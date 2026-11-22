package site.techmoa.worker.scheduler.trigger

import org.springframework.context.annotation.Profile
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import site.techmoa.worker.scheduler.service.ArticleNotificationConsumer

@Component
@Profile("prod", "local")
class NotificationConsumerTrigger(private val consumer: ArticleNotificationConsumer) {
    @Scheduled(fixedDelay = 1000, scheduler = "schedulesTaskScheduler")
    fun run() = consumer.sendPendingNotifications()
}
