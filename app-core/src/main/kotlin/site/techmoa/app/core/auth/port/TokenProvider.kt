package site.techmoa.app.core.auth.port

import site.techmoa.app.core.auth.dto.AuthToken

interface TokenProvider {
    fun issue(memberId: Long): AuthToken
}
