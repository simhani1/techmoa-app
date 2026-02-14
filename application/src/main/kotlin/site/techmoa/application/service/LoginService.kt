package site.techmoa.application.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import site.techmoa.application.dto.AuthToken
import site.techmoa.application.port.AuthTokenPort
import site.techmoa.application.usecase.MemberUseCase
import site.techmoa.application.usecase.OauthUseCase

@Service
class LoginService(
    private val oauthUseCase: OauthUseCase,
    private val memberUseCase: MemberUseCase,
    private val authTokenPort : AuthTokenPort
) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    fun process(code: String): AuthToken {
        log.info("[OAuth] Processing OAuth login with code length: ${code.length}")
        val memberResource = oauthUseCase.fetchMemberResource(code)

        log.info("[OAuth] Member resource received - provider: ${memberResource.provider}, email: ${memberResource.email}, subject: ${memberResource.subject}")
        val member = memberUseCase.getOrSave(memberResource)

        log.info("[OAuth] Member found/created - memberId: ${member.id}")
        val authToken = authTokenPort.issue(member.id)

        log.info("[OAuth] Token issued successfully")
        return authToken
    }
}