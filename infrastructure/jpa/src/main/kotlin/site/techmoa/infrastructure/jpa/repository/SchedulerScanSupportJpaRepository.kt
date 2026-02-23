package site.techmoa.infrastructure.jpa.repository

import org.springframework.data.jpa.repository.JpaRepository
import site.techmoa.infrastructure.jpa.entity.SchedulerScanSupportEntity

interface SchedulerScanSupportJpaRepository : JpaRepository<SchedulerScanSupportEntity, String> {
    fun findByJobName(jobName: String): SchedulerScanSupportEntity?
}
