package site.techmoa.app.core.member

interface KakaoOidcClient {

    fun getToken(code: String): KakaoToken
}