package site.techmoa.app.core.auth.kakao.dto

data class KakaoToken(
    val idToken: String,
    val scope: String,
)
