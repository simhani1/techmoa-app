package site.techmoa.app.core.auth

import org.springframework.stereotype.Service
import site.techmoa.app.core.auth.dto.AuthToken
import site.techmoa.app.core.auth.port.ExternalAuthProvider
import site.techmoa.app.core.auth.port.TokenProvider
import site.techmoa.app.core.member.MemberUseCase

@Service
class OauthLoginUseCase(
    private val externalAuthProvider: ExternalAuthProvider,
    private val memberUseCase: MemberUseCase,
    private val tokenProvider: TokenProvider

) {
    fun process(code: String): AuthToken {
        val memberResource = externalAuthProvider.getMemberResource(code)
        val member = memberUseCase.findOrCreate(memberResource)
        return tokenProvider.issue(member.id)
    }
}
