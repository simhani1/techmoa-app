package site.techmoa.app.infra

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import site.techmoa.app.core.member.KakaoOidcKeyProvider
import java.math.BigInteger
import java.security.KeyFactory
import java.security.PublicKey
import java.security.spec.RSAPublicKeySpec
import java.util.Base64

@Component
class KakaoOidcKeyProviderImpl(
    private val restClientBuilder: RestClient.Builder,
) : KakaoOidcKeyProvider {
    private val objectMapper = jacksonObjectMapper()
    private val urlDecoder = Base64.getUrlDecoder()

    override fun findKey(kid: String): PublicKey {
        val response = restClientBuilder
            .baseUrl(KAKAO_ISSUER)
            .build()
            .get()
            .uri(JWKS_URI)
            .retrieve()
            .body(String::class.java)
            ?: error("Empty JWKS response from Kakao")

        val jwks = objectMapper.readValue(response, JwksResponse::class.java)
        val key = jwks.keys.firstOrNull { it.kid == kid }
            ?: throw IllegalStateException("Kakao JWKS does not contain kid=$kid")

        if (key.kty != "RSA") {
            throw IllegalStateException("Unsupported key type: ${key.kty}")
        }

        val modulus = BigInteger(1, urlDecoder.decode(key.n))
        val exponent = BigInteger(1, urlDecoder.decode(key.e))
        val spec = RSAPublicKeySpec(modulus, exponent)
        return KeyFactory.getInstance("RSA").generatePublic(spec)
    }

    data class JwksResponse(
        @JsonProperty("keys")
        val keys: List<JwkKey>,
    )

    data class JwkKey(
        @JsonProperty("kid")
        val kid: String,
        @JsonProperty("kty")
        val kty: String,
        @JsonProperty("n")
        val n: String,
        @JsonProperty("e")
        val e: String,
    )

    companion object {
        const val KAKAO_ISSUER = "https://kauth.kakao.com"
        const val JWKS_URI = "/.well-known/jwks.json"
    }
}
