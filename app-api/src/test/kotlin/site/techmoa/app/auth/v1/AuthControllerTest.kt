package site.techmoa.app.auth.v1

import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FilterType
import org.springframework.http.HttpHeaders
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import site.techmoa.app.core.auth.OauthLoginUseCase
import site.techmoa.app.core.auth.dto.AuthToken
import site.techmoa.app.core.config.WebConfig
import site.techmoa.app.core.interceptor.JwtInterceptor

@WebMvcTest(
    controllers = [AuthController::class],
    excludeFilters = [
    ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = [JwtInterceptor::class, WebConfig::class] // 제외할 인터셉터 클래스 지정
    )
])
class AuthControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockitoBean
    lateinit var oauthLoginUseCase: OauthLoginUseCase

    @Test
    fun `카카오 로그인 후 Access Token을 헤더로 응답한다`() {
        // GIVEN
        val code = "kakao-code"
        val accessToken = "access-token"

        // WHEN
        given(oauthLoginUseCase.process(code)).willReturn(AuthToken(accessToken))

        val response = mockMvc.get("/v1/oauth/kakao/callback") {
            param("code", code)
        }

        // THEN
        response.andExpect {
            status { isOk() }
            header { string(HttpHeaders.AUTHORIZATION, "Bearer $accessToken") }
        }
    }

}