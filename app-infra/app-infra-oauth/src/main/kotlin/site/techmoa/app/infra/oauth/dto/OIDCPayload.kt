package site.techmoa.app.infra.oauth.dto

data class OIDCPayload(
    val issuer: String,
    val audience: String,
    val subject: String,
    val email: String
)