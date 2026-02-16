package site.techmoa.presentation.common.cookie

import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseCookie
import org.springframework.stereotype.Component

@Component
class AuthCookieFactory {

    fun accessTokenHeaders(accessToken: String): HttpHeaders {
        val headers = HttpHeaders()
        headers.add(HttpHeaders.SET_COOKIE, accessTokenCookie(accessToken).toString())
        return headers
    }

    private fun accessTokenCookie(accessToken: String): ResponseCookie {
        return ResponseCookie.from("accessToken", accessToken)
            .httpOnly(true)
            .secure(true)
            .path("/")
            .sameSite("Lax")
            .build()
    }
}
