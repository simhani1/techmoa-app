package site.techmoa.app.core.auth.dto

data class KakaoToken(
    val idToken: String,
    val scope: String,
)