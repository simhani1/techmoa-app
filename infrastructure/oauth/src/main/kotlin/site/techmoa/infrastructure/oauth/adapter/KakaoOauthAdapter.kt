package site.techmoa.infrastructure.oauth.adapter

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import site.techmoa.application.dto.MemberResource
import site.techmoa.application.port.OauthPort
import site.techmoa.domain.model.OauthProvider
import site.techmoa.infrastructure.oauth.rest.KakaoRestClient
import site.techmoa.infrastructure.oauth.support.oidc.OIDCHelper

@Component
class KakaoOauthAdapter(
    private val kakaoRestClient: KakaoRestClient,
    private val oidcHelper: OIDCHelper
) : OauthPort {

    private val log = LoggerFactory.getLogger(this.javaClass)

    override fun getMemberResource(code: String): MemberResource {
        log.info("[Kakao OAuth] Fetching token from Kakao")
        val kakaoToken = kakaoRestClient.getToken(code)

        log.info("[Kakao OAuth] Token received - idToken length: ${kakaoToken.idToken.length}, scope: ${kakaoToken.scope}")
        log.info("[Kakao OAuth] Fetching public keys from Kakao")
        val keys = kakaoRestClient.getPubKeys()

        log.info("[Kakao OAuth] Public keys received - count: ${keys.keys.size}")
        log.info("[Kakao OAuth] Validating ID token")
        val payload = oidcHelper.getPayload(kakaoToken.idToken, keys)

        log.info("[Kakao OAuth] ID token validated - issuer: ${payload.issuer}, audience: ${payload.audience}, subject: ${payload.subject}, email: ${payload.email}")
        return MemberResource(
            email = payload.email,
            provider = OauthProvider.KAKAO,
            subject = payload.subject
        )
    }
}