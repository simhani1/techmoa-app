package site.techmoa.app.core.auth.port

import site.techmoa.app.core.auth.domain.ExternalUser

interface ExternalAuthProvider {
    fun authenticate(code: String): ExternalUser
}