package site.techmoa.infrastructure.oauth.adapter

import org.springframework.stereotype.Component
import site.techmoa.application.dto.AuthToken
import site.techmoa.application.port.AuthTokenPort
import site.techmoa.infrastructure.oauth.support.jwt.JwtProvider

@Component
class JwtTokenAdapter(
    private val jwtProvider: JwtProvider
) : AuthTokenPort {

    override fun issue(memberId: Long): AuthToken {
        return jwtProvider.issue(memberId)
    }
}