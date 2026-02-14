package site.techmoa.app.common.member.port

import site.techmoa.app.common.auth.OauthProvider
import site.techmoa.app.common.auth.dto.MemberResource
import site.techmoa.app.common.member.domain.Member
import site.techmoa.app.common.member.domain.MemberLookupResult

interface MemberPort {
    fun findByProviderAndSubject(provider: OauthProvider, subject: String): MemberLookupResult
    fun save(resource: MemberResource): Member
}