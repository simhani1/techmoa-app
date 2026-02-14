package site.techmoa.application.port

import site.techmoa.application.dto.AuthToken

interface AuthTokenPort {
    fun issue(memberId: Long): AuthToken
}