package site.techmoa.app.infra.oauth

import io.jsonwebtoken.*
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

    fun getKidFromUnsignedTokenHeader(token: String, iss: String, aud: String): String {
        return getUnsignedTokenClaims(token, iss, aud).header[KID] as String
    }

    private fun getUnsignedTokenClaims(token: String, iss: String, aud: String): Jwt<Header, Claims> {
        try {
            return Jwts.parser()
                .requireIssuer(iss)
                .requireAudience(aud)
                .build()
                .parseUnsecuredClaims(getUnsignedToken(token))
        } catch (e: ExpiredJwtException) {
            throw RuntimeException("mma")
        } catch (e: Exception) {
            log.error(e.toString());
            throw RuntimeException()
        }
    }

    private fun getUnsignedToken(token: String): String {
        val splitToken = token.split("\\.")
        if (splitToken.size != 3) throw InvalidTokenException("Invalid idToken format: $token")
        return splitToken[0] + "." + splitToken[1] + "."
    }

    fun getPayload(token: String, n: String, e: String): OIDCPayload {
        val payload = getTokenJws(token, n, e).payload
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
