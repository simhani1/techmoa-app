package site.techmoa.app.infra.jwt

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import site.techmoa.app.core.auth.dto.AuthToken
import site.techmoa.app.core.auth.port.TokenProvider
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.util.*
import javax.crypto.SecretKey

@Component
class JwtTokenProvider(
    private val property: JwtProperty
) : TokenProvider {

    companion object {
        private const val MEMBER_ID = "memberId"
    }

    override fun issue(memberId: Long): AuthToken {
        val now = Instant.now()
        val expiresAt = now.plusSeconds(property.atkExpirationSeconds)
        val signingKey = Keys.hmacShaKeyFor(property.secret.toByteArray(StandardCharsets.UTF_8))

        val accessToken = getAccessToken(memberId, now, expiresAt, signingKey)

        return AuthToken(accessToken)
    }

    private fun getAccessToken(
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
