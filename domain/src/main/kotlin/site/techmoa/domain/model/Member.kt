package site.techmoa.domain.model

data class Member(
    val id: Long,
    val email: String,
    val provider: OauthProvider,
    val subject: String,
)