package site.techmoa.app.core.auth.jwt

import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import site.techmoa.app.core.auth.dto.AuthToken
import site.techmoa.app.core.auth.exception.InvalidTokenException
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.util.*
import javax.crypto.SecretKey

@Component
class JwtTokenProvider(
    private val property: JwtProperty
) {

    companion object {
        private const val MEMBER_ID = "memberId"
    }

    fun issue(memberId: Long): AuthToken {
        val now = Instant.now()
        val expiresAt = now.plusSeconds(property.atkExpirationSeconds)
        val signingKey = Keys.hmacShaKeyFor(property.secret.toByteArray(StandardCharsets.UTF_8))

        val accessToken = createAccessToken(memberId, now, expiresAt, signingKey)

        return AuthToken(accessToken)
    }

    fun parse(token: String): Long {
        val signingKey = Keys.hmacShaKeyFor(property.secret.toByteArray(StandardCharsets.UTF_8))

        return try {
            Jwts.parser()
                .requireIssuer(property.issuer)
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .payload
                .get(MEMBER_ID, Long::class.java)
        } catch (e: ExpiredJwtException) {
            throw InvalidTokenException("Token expired: ${e.message}")
        } catch (e: Exception) {
            throw InvalidTokenException("Invalid token: ${e.message}")
        }
    }

    private fun createAccessToken(
        memberId: Long,
        now: Instant,
        expiresAt: Instant,
        signingKey: SecretKey
    ): String = Jwts.builder()
        .issuer(property.issuer)
        .subject(memberId.toString())
        .issuedAt(Date.from(now))
        .expiration(Date.from(expiresAt))
        .claim(MEMBER_ID, memberId)
        .signWith(signingKey)
        .compact()
}
