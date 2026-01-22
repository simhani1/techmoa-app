package site.techmoa.app.auth.v1

import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import site.techmoa.app.common.auth.OauthLoginUseCase
import site.techmoa.app.common.response.ApiResponse

@RestController
class AuthController(
    private val oauthLoginUseCase: OauthLoginUseCase
) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @GetMapping("/v1/oauth/kakao/callback")
    fun kakaoAuthorization(
        @RequestParam("code") code: String,
    ): ResponseEntity<ApiResponse<Any>> {
        log.info("[OAuth] Kakao authorization callback received, code length: ${code.length}")
        val token = oauthLoginUseCase.process(code)
        log.info("[OAuth] Login successful, access token issued for memberId: masked")
        val headers = HttpHeaders()
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer ${token.accessToken}")
        return ResponseEntity(
            headers,
            HttpStatus.OK
        )
    }
}
