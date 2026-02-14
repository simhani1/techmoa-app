package site.techmoa.app.common.member.domain

import site.techmoa.app.common.auth.OauthProvider

data class Member(
    val id: Long,
    val email: String,
    val provider: OauthProvider,
    val subject: String,
)