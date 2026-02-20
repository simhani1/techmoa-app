package site.techmoa.infrastructure.jpa.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "last_scanned_article_id")
class LastScannedArticleIdEntity(
    @Id
    @Column(name = "job_name", nullable = false, length = 100)
    val jobName: String,

    @Column(name = "last_scanned_article_id", nullable = false)
    private var _lastScannedArticleId: Long
) : BaseEntity() {

    val lastScannedArticleId: Long
        get() = _lastScannedArticleId

    fun updateLastScannedArticleId(value: Long) {
        _lastScannedArticleId = value
    }

    companion object {
        fun of(jobName: String, lastScannedArticleId: Long): LastScannedArticleIdEntity {
            return LastScannedArticleIdEntity(
                jobName = jobName,
                _lastScannedArticleId = lastScannedArticleId
            )
        }
    }
}
