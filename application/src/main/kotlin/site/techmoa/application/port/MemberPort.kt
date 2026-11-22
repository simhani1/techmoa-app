package site.techmoa.application.port

import site.techmoa.application.dto.MemberResource
import site.techmoa.domain.model.Member
import site.techmoa.domain.model.MemberLookupResult
import site.techmoa.domain.model.OauthProvider

interface MemberPort {
    fun findByProviderAndSubject(provider: OauthProvider, subject: String): MemberLookupResult
    fun save(resource: MemberResource): Member
    fun existsByLoginId(loginId: String): Boolean
    fun saveLocal(loginId: String, encodedPassword: String): Member
}