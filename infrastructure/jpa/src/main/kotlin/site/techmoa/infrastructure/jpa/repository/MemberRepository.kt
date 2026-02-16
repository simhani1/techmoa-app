package site.techmoa.infrastructure.jpa.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import site.techmoa.domain.model.OauthProvider
import site.techmoa.infrastructure.jpa.entity.MemberEntity

interface MemberRepository : JpaRepository<MemberEntity, Long> {
    @Query("SELECT m FROM MemberEntity m WHERE m.provider = :provider and m.subject = :subject")
    fun findByProviderAndSubjectOrNull(@Param("provider") provider: OauthProvider, @Param("subject") subject: String): MemberEntity?
}