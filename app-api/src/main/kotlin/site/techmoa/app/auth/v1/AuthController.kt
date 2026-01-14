package site.techmoa.app.auth.v1

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import site.techmoa.app.auth.v1.response.LoginResponse
import site.techmoa.app.core.member.KakaoLoginOrSignupFacade
import site.techmoa.app.core.response.ApiResponse

@RestController
class AuthController(
    private val kakaoLoginOrSignupFacade: KakaoLoginOrSignupFacade
) {

    @GetMapping("/v1/oauth/kakao/callback")
    fun kakaoAuthorization(
        @RequestParam("code") code: String,
    ): ApiResponse<LoginResponse> {
        val jwtToken = kakaoLoginOrSignupFacade.execute(code)
        return ApiResponse.success(LoginResponse(jwtToken.accessToken))
    }
}
