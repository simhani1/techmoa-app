package site.techmoa.infrastructure.oauth.support.oidc

data class OIDCPayload(
    val issuer: String,
    val audience: String,
    val subject: String,
    val email: String
)