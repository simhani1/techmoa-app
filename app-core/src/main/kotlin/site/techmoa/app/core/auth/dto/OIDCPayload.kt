package site.techmoa.app.core.auth.dto

data class OIDCPayload(
    val issuer: String,
    val audience: String,
    val subject: String,
    val email: String
)