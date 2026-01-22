package site.techmoa.app.storage.db.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import site.techmoa.app.common.auth.OauthProvider
import site.techmoa.app.storage.db.entity.MemberEntity

interface MemberRepository : JpaRepository<MemberEntity, Long> {
    @Query("SELECT m FROM MemberEntity m WHERE m.provider = :provider and m.subject = :subject")
    fun findByProviderAndSubjectOrNull(@Param("provider") provider: OauthProvider, @Param("subject") subject: String): MemberEntity?
}