package site.techmoa.application.dto

import site.techmoa.domain.model.OauthProvider

data class MemberResource(
    val email: String,
    val provider: OauthProvider,
    val subject: String,
)