package site.techmoa.worker.scheduler.usecase

import org.springframework.stereotype.Component
import site.techmoa.worker.scheduler.dto.LastScannedArticleDto
import site.techmoa.worker.scheduler.repository.LastScannedArticleRepository

@Component
class LastScannedArticleUseCase(
    private val repository: LastScannedArticleRepository
) {

    companion object {
        private const val PUBLISH_NOTIFICATION_JOB = "publish_notification_job"
    }

    fun getLastScannedArticle(): LastScannedArticleDto {
        return repository.findBy(PUBLISH_NOTIFICATION_JOB)
            ?: LastScannedArticleDto()
    }

    fun sync(id: Long) {
        repository.sync(PUBLISH_NOTIFICATION_JOB, id)
    }
}
