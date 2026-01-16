package site.techmoa.app.core.auth.dto

import site.techmoa.app.core.auth.OauthProvider

data class MemberResource(
    val email: String,
    val provider: OauthProvider,
    val subject: String,
)