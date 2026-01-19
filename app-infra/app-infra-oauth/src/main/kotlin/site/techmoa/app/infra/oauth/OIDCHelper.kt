package site.techmoa.app.infra.oauth

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import site.techmoa.app.infra.oauth.dto.OIDCPayload
import site.techmoa.app.infra.oauth.dto.OIDCPubKeyDto
import site.techmoa.app.infra.oauth.dto.OIDCPubKeys

@Component
class OIDCHelper(
    private val jwtOIDCProvider: JwtOIDCProvider,
    properties: OauthProperties
) {

    private val log = LoggerFactory.getLogger(this.javaClass)
    private val property = properties.kakao

    fun getPayload(idToken: String, pubKeys: OIDCPubKeys): OIDCPayload {
        log.info("[OIDC] Extracting kid from unsigned token - expected issuer: ${property.baseUrl}, expected audience: ${property.clientId}")
        val kid = getKidFromUnsignedToken(idToken, property.baseUrl, property.clientId)
        log.info("[OIDC] Kid extracted: $kid")
        log.info("[OIDC] Matching public key for kid: $kid")
        val pubKey = getPubKey(pubKeys, kid)
        log.info("[OIDC] Public key matched - alg: ${pubKey.alg}, use: ${pubKey.use}")
        log.info("[OIDC] Extracting payload with public key")
        return getPayload(idToken, pubKey)
    }

    private fun getKidFromUnsignedToken(
        idToken: String,
        iss: String,
        aud: String
    ): String = jwtOIDCProvider.getKidFromUnsignedTokenHeader(idToken, iss, aud)

    private fun getPubKey(
        pubKeys: OIDCPubKeys,
        kid: String
    ): OIDCPubKeyDto = pubKeys.keys.firstOrNull { it.kid == kid } ?: throw RuntimeException("No matched keys for kid: $kid")

    private fun getPayload(
        idToken: String,
        pubKey: OIDCPubKeyDto
    ): OIDCPayload = jwtOIDCProvider.getPayload(idToken, pubKey.n, pubKey.e)
}