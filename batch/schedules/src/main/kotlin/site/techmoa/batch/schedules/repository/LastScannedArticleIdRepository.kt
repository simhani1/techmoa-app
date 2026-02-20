package site.techmoa.batch.schedules.repository

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import site.techmoa.infrastructure.jpa.entity.LastScannedArticleIdEntity
import site.techmoa.infrastructure.jpa.repository.LastScannedArticleIdJpaRepository

@Repository
class LastScannedArticleIdRepository(
    private val lastScannedArticleIdJpaRepository: LastScannedArticleIdJpaRepository
) {

    @Transactional(readOnly = true)
    fun findLastScannedArticleId(jobName: String): Long? {
        return lastScannedArticleIdJpaRepository
            .findByIdOrNull(jobName)
            ?.lastScannedArticleId
    }

    @Transactional
    fun upsert(jobName: String, lastScannedArticleId: Long) {
        val existing = lastScannedArticleIdJpaRepository.findByIdOrNull(jobName)
        if (existing != null) {
            existing.updateLastScannedArticleId(lastScannedArticleId)
            return
        }

        lastScannedArticleIdJpaRepository.save(
            LastScannedArticleIdEntity.of(
                jobName = jobName,
                lastScannedArticleId = lastScannedArticleId
            )
        )
    }
}
