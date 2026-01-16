package site.techmoa.app.infra.oauth

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