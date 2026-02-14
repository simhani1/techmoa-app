package site.techmoa.app.common.auth.dto

data class KakaoToken(
    val idToken: String,
    val scope: String,
)