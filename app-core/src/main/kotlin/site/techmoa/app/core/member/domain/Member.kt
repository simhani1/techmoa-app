package site.techmoa.app.core.member.domain

import site.techmoa.app.core.auth.OauthProvider

data class Member(
    val id: Long,
    val email: String,
    val provider: OauthProvider,
    val subject: String,
)