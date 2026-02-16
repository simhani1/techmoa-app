package site.techmoa.infrastructure.oauth.support.jwt

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "jwt")
data class JwtProperty(
    val issuer: String,
    val secret: String,
    val atkExpirationSeconds: Long
)