package site.techmoa.app.common.auth.jwt

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "jwt")
class JwtProperty(
    val issuer: String,
    val secret: String,
    val atkExpirationSeconds: Long
)