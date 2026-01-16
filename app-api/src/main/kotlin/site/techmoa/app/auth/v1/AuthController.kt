package site.techmoa.app.auth.v1

import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import site.techmoa.app.auth.v1.response.LoginResponse
import site.techmoa.app.core.auth.OauthLoginUseCase
import site.techmoa.app.core.response.ApiResponse

@RestController
class AuthController(
    private val oauthLoginUseCase: OauthLoginUseCase
) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @GetMapping("/v1/oauth/kakao/callback")
    fun kakaoAuthorization(
        @RequestParam("code") code: String,
    ): ApiResponse<LoginResponse> {
        log.info("[OAuth] Kakao authorization callback received, code length: ${code.length}")
        val token = oauthLoginUseCase.process(code)
        log.info("[OAuth] Login successful, access token issued for memberId: masked")
        return ApiResponse.success(LoginResponse(token.accessToken))
    }
}
