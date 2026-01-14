package site.techmoa.app.core.member

import org.springframework.stereotype.Component

@Component
class KakaoOidcExchanger(
    private val kakaoOidcClient: KakaoOidcClient
) {
    fun retrieve(code: String): KakaoToken {
        return kakaoOidcClient.getToken(code)
    }
}