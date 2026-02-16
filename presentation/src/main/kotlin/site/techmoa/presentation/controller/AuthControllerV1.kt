package site.techmoa.presentation.controller

import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import site.techmoa.application.service.LoginService
import site.techmoa.presentation.common.cookie.AuthCookieFactory
import site.techmoa.presentation.common.template.ApiResponse

@RequestMapping("/v1/oauth")
@RestController
class AuthControllerV1(
    private val loginService: LoginService,
    private val authCookieFactory: AuthCookieFactory,
) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @GetMapping("/kakao/callback")
    fun kakaoAuthorization(
        @RequestParam("code") code: String,
    ): ResponseEntity<ApiResponse<Any>> {
        log.info("[OAuth] Kakao authorization callback received, code length: ${code.length}")
        val token = loginService.process(code)

        log.info("[OAuth] Login successful, access token issued")
        val headers = authCookieFactory.accessTokenHeaders(token.accessToken)
        return ResponseEntity.ok()
            .headers(headers)
            .body(ApiResponse.success())
    }
}
