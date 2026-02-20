package site.techmoa.batch.schedules.usecase

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class PublishNotificationMessageUseCase {

    private val log = LoggerFactory.getLogger(this.javaClass)

    companion object {
        private const val JOB_NAME = "publish_notification_message_scheduler"
        private const val ARTICLE_SCAN_SIZE = 100
    }

    @Transactional
    fun execute() {
        log.info(
            "[{}] Execute scheduler jobName={}, articleScanSize={}",
            this.javaClass.simpleName,
            JOB_NAME,
            ARTICLE_SCAN_SIZE
        )
    }
}