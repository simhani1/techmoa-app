package site.techmoa.app.infra.oauth.kakao

import org.springframework.stereotype.Component
import site.techmoa.app.core.auth.OauthProvider
import site.techmoa.app.core.auth.dto.MemberResource
import site.techmoa.app.core.auth.port.ExternalAuthProvider
import site.techmoa.app.infra.oauth.OIDCHelper

@Component
class KakaoAuthProvider(
    private val kakaoRestClient: KakaoRestClient,
    private val oidcHelper: OIDCHelper
) : ExternalAuthProvider {
    override fun getMemberResource(code: String): MemberResource {
        val kakaoToken = kakaoRestClient.getToken(code)
        val keys = kakaoRestClient.getPubKeys()
        val payload = oidcHelper.getPayload(kakaoToken.idToken, keys)
        return MemberResource(
            email = payload.email,
            provider = OauthProvider.KAKAO,
            subject = payload.subject
        )
    }
}