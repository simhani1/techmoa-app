package site.techmoa.app.infra

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.client.ClientHttpResponse
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.client.body
import site.techmoa.app.core.member.KakaoOidcClient
import site.techmoa.app.core.member.KakaoToken
import site.techmoa.app.infra.exception.KakaoClientException
import site.techmoa.app.infra.exception.KakaoServerException

@Component
class KakaoClient(
    @Value("\${oauth.kakao.client-id}") private val clientId: String,
    @Value("\${oauth.kakao.client-secret}") private val clientSecret: String,
    @Value("\${oauth.kakao.redirect-uri}") private val redirectUri: String,
): KakaoOidcClient {

    companion object {
        const val BASE_URL = "https://kauth.kakao.com"
        const val GET_TOKEN_URI = "/oauth/token"
        const val GRANT_TYPE = "authorization_code"
    }

    override fun getToken(code: String): KakaoToken {
        val restClient = RestClient.builder()
            .baseUrl(BASE_URL)
            .build()

        val response = restClient.post()
            .uri { uriBuilder ->
                uriBuilder
                    .path(GET_TOKEN_URI)
                    .queryParam("grant_type", GRANT_TYPE)
                    .queryParam("client_id", clientId)
                    .queryParam("client_secret", clientSecret)
                    .queryParam("redirect_uri", redirectUri)
                    .queryParam("code", code)
                    .build()
            }
            .retrieve()
            .onStatus({ status -> status.is4xxClientError }) { _, response ->
                throw KakaoClientException(
                    "Get Kakao token request failed with 4xx: ${response.statusCode} ${readBody(response)}"
                )
            }
            .onStatus({ status -> status.is5xxServerError }) { _, response ->
                throw KakaoServerException(
                    "Get Kakao token request failed with 5xx: ${response.statusCode} ${readBody(response)}"
                )
            }
            .body<TokenResponse>()!!

        return KakaoToken(response.idToken, response.scope)
    }

    private fun readBody(response: ClientHttpResponse): String {
        return String(response.body.readAllBytes(), Charsets.UTF_8)
    }

    data class TokenResponse(
        @JsonProperty("access_token")
        val accessToken: String,
        @JsonProperty("token_type")
        val tokenType: String,
        @JsonProperty("refresh_token")
        val refreshToken: String,
        @JsonProperty("id_token")
        val idToken: String,
        @JsonProperty("expires_in")
        val expiresIn: Long,
        @JsonProperty("scope")
        val scope: String,
        @JsonProperty("refresh_token_expires_in")
        val refreshTokenExpiresIn: Long,
    ){
}
}

