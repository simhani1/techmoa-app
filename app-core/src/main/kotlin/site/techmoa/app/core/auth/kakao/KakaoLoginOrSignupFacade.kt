package site.techmoa.app.core.auth.kakao

import org.springframework.stereotype.Service
import site.techmoa.app.core.auth.kakao.dto.JwtToken
import site.techmoa.app.core.auth.kakao.port.KakaoOidcClient

@Service
class KakaoLoginOrSignupFacade(
    private val kakaoOidcClient: KakaoOidcClient,
    private val kakaoIdTokenValidator: KakaoIdTokenValidator,
) {

    fun execute(code: String): JwtToken {
        // 1. 인가 코드로 카카오 토큰 발급
        val kakaoToken = kakaoOidcClient.getToken(code)

        // 2. OIDC 유효성 검사 && 페이로드 획득
        val claims = kakaoIdTokenValidator.validate(kakaoToken.idToken)

        // 4. 회원가입

        // 5. 로그인
        return JwtToken("ff")
    }
}
