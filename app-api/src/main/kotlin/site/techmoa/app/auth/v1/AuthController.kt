package site.techmoa.app.auth.v1

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

    @GetMapping("/v1/oauth/kakao/callback")
    fun kakaoAuthorization(
        @RequestParam("code") code: String,
    ): ApiResponse<LoginResponse> {
        val jwtToken = oauthLoginUseCase.process(code)
        return ApiResponse.success(LoginResponse(jwtToken.accessToken))
    }
}
