package site.techmoa.app.infra.oauth

import org.springframework.stereotype.Component
import site.techmoa.app.infra.oauth.dto.OIDCPayload
import site.techmoa.app.infra.oauth.dto.OIDCPubKeyDto
import site.techmoa.app.infra.oauth.dto.OIDCPubKeys

@Component
class OIDCHelper(
    private val jwtOIDCProvider: JwtOIDCProvider,
    properties: OauthProperties
) {

    private val property = properties.kakao

    fun getPayload(idToken: String, pubKeys: OIDCPubKeys): OIDCPayload {
        val kid = getKidFromUnsignedToken(idToken, property.baseUrl, property.clientId)
        val pubKey = getPubKey(pubKeys, kid)
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
    ): OIDCPubKeyDto = pubKeys.keys.firstOrNull { it.kid == kid } ?: throw RuntimeException("No matched keys")

    private fun getPayload(
        idToken: String,
        pubKey: OIDCPubKeyDto
    ): OIDCPayload = jwtOIDCProvider.getPayload(idToken, pubKey.n, pubKey.e)
}