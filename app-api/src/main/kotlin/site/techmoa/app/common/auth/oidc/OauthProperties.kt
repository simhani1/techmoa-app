package site.techmoa.app.common.auth.oidc

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "oauth")
data class OauthProperties(
    val kakao: OAuthSecret
) {
    data class OAuthSecret(
        val baseUrl: String,
        val clientId: String,
        val clientSecret: String,
        val redirectUri: String,
    )
}