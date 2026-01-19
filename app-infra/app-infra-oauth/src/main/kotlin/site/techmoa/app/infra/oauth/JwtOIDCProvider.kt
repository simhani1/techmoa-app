package site.techmoa.app.infra.oauth

import com.fasterxml.jackson.databind.ObjectMapper
import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jws
import io.jsonwebtoken.Jwts
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import site.techmoa.app.infra.jwt.InvalidTokenException
import site.techmoa.app.infra.oauth.dto.OIDCPayload
import java.math.BigInteger
import java.security.KeyFactory
import java.security.PublicKey
import java.security.spec.RSAPublicKeySpec
import java.util.*

@Component
class JwtOIDCProvider {

    companion object {
        private const val KID = "kid"
        private const val RSA = "RSA"
        private const val EMAIL = "email"
    }

    private val log = LoggerFactory.getLogger(this.javaClass)
    private val objectMapper = ObjectMapper()

    fun getKidFromUnsignedTokenHeader(token: String, iss: String, aud: String): String {
        log.info("[JWT] Extracting kid from unsigned token header - token length: ${token.length}, expected issuer: $iss, expected audience: $aud")
        try {
            val splitToken = token.split('.')
            log.info("[JWT] Splitting token - parts: ${splitToken.size}")
            if (splitToken.size != 3) {
                log.error("[JWT] Invalid token format - expected 3 parts, got ${splitToken.size}")
                throw InvalidTokenException("Invalid idToken format: expected 3 parts, got ${splitToken.size}")
            }
            val headerJson = String(Base64.getUrlDecoder().decode(splitToken[0]))
            val kid = objectMapper.readTree(headerJson).path(KID).asText()
            if (kid.isBlank()) {
                log.error("[JWT] Missing kid in token header")
                throw InvalidTokenException("Invalid idToken format: missing kid in token header")
            }
            log.info("[JWT] kid extracted from token header")
            return kid
        } catch (e: Exception) {
            log.error("[JWT] Failed to extract kid from token header", e)
            throw RuntimeException("Failed to extract kid: ${e.message}")
        }
    }

    fun getPayload(token: String, n: String, e: String): OIDCPayload {
        log.info("[JWT] Extracting payload from signed token - token length: ${token.length}")
        val payload = getTokenJws(token, n, e).payload
        log.info("[JWT] Payload extracted successfully - issuer: ${payload.issuer}, audience: ${payload.audience}, subject: ${payload.subject}, email: ${payload[EMAIL]}")
        return OIDCPayload(
            issuer = payload.issuer,
            audience = payload.audience.toString(),
            subject = payload.subject,
            email = payload[EMAIL].toString()
        )
    }

    private fun getTokenJws(token: String, n: String, e: String): Jws<Claims> {
        try {
            return Jwts.parser()
                .verifyWith(getRSAPublicKey(n, e))
                .build()
                .parseSignedClaims(token)
        } catch (e: ExpiredJwtException) {
            log.error("Expired JWT token", e)
            throw RuntimeException("mma")
        } catch (e: Exception) {
            log.error("Exception while parsing JWT token", e);
            throw RuntimeException("mma")
        }
    }

    private fun getRSAPublicKey(n: String, e: String): PublicKey {
        val decodedN = Base64.getUrlDecoder().decode(n)
        val decodedE = Base64.getUrlDecoder().decode(e)
        val n = BigInteger(1, decodedN)
        val e = BigInteger(1, decodedE)

        val keyFactory = KeyFactory.getInstance(RSA)
        val keySpec = RSAPublicKeySpec(n, e)

        return keyFactory.generatePublic(keySpec)
    }
}
