package site.techmoa.app.core.auth.kakao.port

import site.techmoa.app.core.auth.kakao.dto.KakaoToken

interface KakaoOidcClient {

    fun getToken(code: String): KakaoToken
}