package site.techmoa.app.infra.oauth.kakao

import org.springframework.stereotype.Component
import site.techmoa.app.core.auth.domain.ExternalUser
import site.techmoa.app.core.auth.port.ExternalAuthProvider
import site.techmoa.app.infra.oauth.OIDCHelper

@Component
class KakaoAuthProvider(
    private val kakaoRestClient: KakaoRestClient,
    private val oidcHelper: OIDCHelper
) : ExternalAuthProvider {
    override fun authenticate(code: String): ExternalUser {
        val kakaoToken = kakaoRestClient.getToken(code)
        val keys = kakaoRestClient.getPubKeys()
        val payload = oidcHelper.getPayload(kakaoToken.idToken, keys)
        return ExternalUser(payload.email)
    }
}