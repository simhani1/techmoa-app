package site.techmoa.infrastructure.oauth.support.oidc

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import site.techmoa.domain.exception.KidNotMatchException
import site.techmoa.infrastructure.oauth.config.OauthProperties

@Component
class OIDCHelper(
    private val oidcProvider: OIDCProvider,
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
    ): String = oidcProvider.getKidFromUnsignedTokenHeader(idToken, iss, aud)

    private fun getPubKey(
        pubKeys: OIDCPubKeys,
        kid: String
    ): OIDCPubKeyDto = pubKeys.keys.firstOrNull { it.kid == kid } ?: throw KidNotMatchException("No matched keys for kid: $kid")

    private fun getPayload(
        idToken: String,
        pubKey: OIDCPubKeyDto
    ): OIDCPayload = oidcProvider.getPayload(idToken, pubKey.n, pubKey.e)
}