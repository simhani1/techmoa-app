package site.techmoa.app.core.member.port

import site.techmoa.app.core.auth.OauthProvider
import site.techmoa.app.core.auth.dto.MemberResource
import site.techmoa.app.core.member.domain.Member
import site.techmoa.app.core.member.domain.MemberLookupResult

interface MemberPort {
    fun findByProviderAndSubject(provider: OauthProvider, subject: String): MemberLookupResult
    fun save(resource: MemberResource): Member
}