package site.techmoa.app.core.auth.kakao

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.security.PublicKey
import java.security.Signature
import java.time.Clock
import java.time.Instant
import java.util.*

@Component
class KakaoIdTokenValidator(
    private val kakaoOidcKeyProvider: KakaoOidcKeyProvider,
    @Value("\${oauth.kakao.client-id}") private val clientId: String,
    private val clock: Clock = Clock.systemUTC(),
) {
    private val objectMapper = jacksonObjectMapper()
    private val urlDecoder = Base64.getUrlDecoder()

    fun validate(idToken: String, expectedNonce: String? = null): KakaoIdTokenClaims {
        val parts = idToken.split(".")
        if (parts.size != 3) {
            throw InvalidIdTokenException("Invalid ID token format")
        }

        val headerJson = String(urlDecoder.decode(parts[0]), Charsets.UTF_8)
        val payloadJson = String(urlDecoder.decode(parts[1]), Charsets.UTF_8)
        val signatureBytes = urlDecoder.decode(parts[2])

        val header = readMap(headerJson)
        val payload = readMap(payloadJson)

        val alg = header["alg"] as? String ?: throw InvalidIdTokenException("Missing alg in ID token header")
        val typ = header["typ"] as? String ?: throw InvalidIdTokenException("Missing typ in ID token header")
        val kid = header["kid"] as? String ?: throw InvalidIdTokenException("Missing kid in ID token header")
        if (alg != "RS256" || typ != "JWT") {
            throw InvalidIdTokenException("Unsupported ID token header: alg=$alg, typ=$typ")
        }

        val issuer = payload["iss"] as? String ?: throw InvalidIdTokenException("Missing iss in ID token payload")
        if (issuer != KAKAO_ISSUER) {
            throw InvalidIdTokenException("Invalid issuer: $issuer")
        }

        val audiences = parseAudiences(payload["aud"])
        if (!audiences.contains(clientId)) {
            throw InvalidIdTokenException("Invalid audience: $audiences")
        }

        val exp = (payload["exp"] as? Number)?.toLong()
            ?: throw InvalidIdTokenException("Missing exp in ID token payload")
        val now = Instant.now(clock).epochSecond
        if (exp <= now) {
            throw InvalidIdTokenException("ID token expired: exp=$exp now=$now")
        }

        val nonce = payload["nonce"] as? String
        if (expectedNonce != null && expectedNonce != nonce) {
            throw InvalidIdTokenException("Invalid nonce")
        }

        val publicKey = kakaoOidcKeyProvider.findKey(kid)
        verifySignature(parts[0], parts[1], signatureBytes, publicKey)

        return KakaoIdTokenClaims(
            issuer = issuer,
            audience = audiences,
            subject = payload["sub"] as? String,
            email = payload["email"] as? String,
            nickname = payload["nickname"] as? String,
            issuedAt = (payload["iat"] as? Number)?.toLong(),
            authTime = (payload["auth_time"] as? Number)?.toLong(),
            expiresAt = exp,
            nonce = nonce,
        )
    }

    private fun verifySignature(
        headerPart: String,
        payloadPart: String,
        signatureBytes: ByteArray,
        publicKey: PublicKey,
    ) {
        val signingInput = "$headerPart.$payloadPart".toByteArray(Charsets.US_ASCII)
        val verifier = Signature.getInstance("SHA256withRSA")
        verifier.initVerify(publicKey)
        verifier.update(signingInput)
        if (!verifier.verify(signatureBytes)) {
            throw InvalidIdTokenException("Invalid ID token signature")
        }
    }

    private fun readMap(json: String): Map<String, Any> {
        return objectMapper.readValue(json, object : TypeReference<Map<String, Any>>() {})
    }

    private fun parseAudiences(rawAud: Any?): List<String> {
        return when (rawAud) {
            is String -> listOf(rawAud)
            is Collection<*> -> rawAud.filterIsInstance<String>()
            else -> emptyList()
        }
    }

    companion object {
        const val KAKAO_ISSUER = "https://kauth.kakao.com"
    }
}

interface KakaoOidcKeyProvider {
    fun findKey(kid: String): PublicKey
}

data class KakaoIdTokenClaims(
    val issuer: String,
    val audience: List<String>,
    val subject: String?,
    val email: String?,
    val nickname: String?,
    val issuedAt: Long?,
    val authTime: Long?,
    val expiresAt: Long,
    val nonce: String?,
)

class InvalidIdTokenException(message: String) : RuntimeException(message)
