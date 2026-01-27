package site.techmoa.app.core.auth

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import site.techmoa.app.core.auth.dto.AuthToken
import site.techmoa.app.core.auth.jwt.JwtTokenProvider
import site.techmoa.app.core.auth.port.ExternalAuthProvider
import site.techmoa.app.core.member.MemberUseCase

@Service
class OauthLoginUseCase(
    private val externalAuthProvider: ExternalAuthProvider,
    private val memberUseCase: MemberUseCase,
    private val jwtTokenProvider: JwtTokenProvider

) {
    private val log = LoggerFactory.getLogger(this.javaClass)

    fun process(code: String): AuthToken {
        log.info("[OAuth] Processing OAuth login with code length: ${code.length}")
        val memberResource = externalAuthProvider.getMemberResource(code)
        log.info("[OAuth] Member resource received - provider: ${memberResource.provider}, email: ${memberResource.email}, subject: ${memberResource.subject}")
        val member = memberUseCase.findOrCreate(memberResource)
        log.info("[OAuth] Member found/created - memberId: ${member.id}")
        val authToken = jwtTokenProvider.issue(member.id)
        log.info("[OAuth] Token issued successfully")
        return authToken
    }
}
