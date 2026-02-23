package site.techmoa.infrastructure.jpa.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "scheduler_scan_support")
class SchedulerScanSupportEntity(
    @Id
    @Column(name = "job_name", nullable = false, length = 100)
    val jobName: String,

    @Column(name = "last_scanned_id", nullable = false)
    private var _lastScannedId: Long
) : BaseEntity() {

    val lastScannedId: Long
        get() = _lastScannedId

    fun updateLastScannedId(value: Long) {
        _lastScannedId = value
    }

    companion object {
        fun of(jobName: String, lastScannedId: Long): SchedulerScanSupportEntity {
            return SchedulerScanSupportEntity(
                jobName = jobName,
                _lastScannedId = lastScannedId
            )
        }
    }
}
