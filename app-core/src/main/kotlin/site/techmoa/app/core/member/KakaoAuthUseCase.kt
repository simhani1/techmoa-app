package site.techmoa.app.core.member

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class KakaoAuthUseCase {

    @Transactional
    fun execute(code: String): JwtToken {
        // 1. 인가 코드로 카카오 토큰 발급

        // 2. OIDC 유효성 검사

        // 3. OIDC 사용자 정보 추출

        // 4. 회원가입

        // 5. JWT 발급
        return JwtToken(TODO())
    }
}