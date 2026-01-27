package site.techmoa.app.infra.oauth.kakao

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.http.client.ClientHttpResponse
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.client.body
import site.techmoa.app.core.auth.dto.KakaoToken
import site.techmoa.app.core.auth.dto.OIDCPubKeyDto
import site.techmoa.app.core.auth.dto.OIDCPubKeys
import site.techmoa.app.core.auth.oidc.OauthProperties
import site.techmoa.app.infra.oauth.exception.KakaoClientException
import site.techmoa.app.infra.oauth.exception.KakaoServerException

@Component
class KakaoRestClient(
    properties: OauthProperties,
    restClientBuilder: RestClient.Builder,
) {

    companion object {
        const val GET_TOKEN_URI = "/oauth/token"
        const val GET_PUB_KEYS_URI = "/.well-known/jwks.json"
        const val GRANT_TYPE = "authorization_code"
    }

    private val property = properties.kakao
    private val restClient = restClientBuilder.baseUrl(property.baseUrl).build()

    fun getToken(code: String): KakaoToken {
        val response = restClient.post()
            .uri { uriBuilder ->
                uriBuilder
                    .path(GET_TOKEN_URI)
                    .queryParam("grant_type", GRANT_TYPE)
                    .queryParam("client_id", property.clientId)
                    .queryParam("client_secret", property.clientSecret)
                    .queryParam("redirect_uri", property.redirectUri)
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

    fun getPubKeys(): OIDCPubKeys {
        val response = restClient.get()
            .uri(GET_PUB_KEYS_URI)
            .retrieve()
            .body<PubKeysResponse>()!!

        return OIDCPubKeys(
            keys = response.keys.map {
                OIDCPubKeyDto(
                    kid = it.kid,
                    alg = it.alg,
                    use = it.use,
                    n = it.n,
                    e = it.e,
                )
            }
        )
    }

    private data class TokenResponse(
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
    )

    private data class PubKeysResponse(
        @JsonProperty("keys")
        val keys: List<PubKeyResponse>,
    )

    private data class PubKeyResponse(
        @JsonProperty("kid")
        val kid: String,
        @JsonProperty("kty")
        val kty: String,
        @JsonProperty("alg")
        val alg: String,
        @JsonProperty("use")
        val use: String,
        @JsonProperty("n")
        val n: String,
        @JsonProperty("e")
        val e: String,
    )
}