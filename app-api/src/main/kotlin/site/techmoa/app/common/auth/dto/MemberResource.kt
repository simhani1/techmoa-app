package site.techmoa.app.common.auth.dto

import site.techmoa.app.common.auth.OauthProvider

data class MemberResource(
    val email: String,
    val provider: OauthProvider,
    val subject: String,
)