package site.techmoa.presentation.controller

import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import site.techmoa.application.dto.AuthToken
import site.techmoa.application.dto.SignupCommand
import site.techmoa.application.service.SignupService
import site.techmoa.domain.exception.DuplicatedLoginIdException
import site.techmoa.presentation.common.cookie.AuthCookieFactory
import site.techmoa.presentation.common.error.GlobalExceptionHandler

class SignupControllerV1Test : BehaviorSpec({
    val objectMapper = ObjectMapper()
    val signupService = mockk<SignupService>()
    val authCookieFactory = AuthCookieFactory()
    val controller = SignupControllerV1(signupService, authCookieFactory)
    val mockMvc: MockMvc = MockMvcBuilders
        .standaloneSetup(controller)
        .setControllerAdvice(GlobalExceptionHandler())
        .build()

    beforeTest {
        clearMocks(signupService)
    }

    given("POST /v1/auth/signup 요청 처리 상황에서") {

        `when`("유효한 loginId와 password로 요청하면") {
            then("201과 accessToken 쿠키를 반환한다") {
                every {
                    signupService.process(SignupCommand(LOGIN_ID, RAW_PASSWORD))
                } returns AuthToken(ACCESS_TOKEN)

                mockMvc.post("/v1/auth/signup") {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(
                        mapOf("loginId" to LOGIN_ID, "password" to RAW_PASSWORD)
                    )
                }
                    .andExpect {
                        status { isCreated() }
                        jsonPath("$.resultType") { value("SUCCESS") }
                        jsonPath("$.data") { doesNotExist() }
                        header { exists("Set-Cookie") }
                        header { string("Set-Cookie", org.hamcrest.Matchers.containsString("accessToken=$ACCESS_TOKEN")) }
                    }

                verify(exactly = 1) {
                    signupService.process(SignupCommand(LOGIN_ID, RAW_PASSWORD))
                }
            }
        }

        `when`("다른 유효한 loginId로 요청하면") {
            then("201과 해당 회원의 accessToken 쿠키를 반환한다") {
                val anotherLoginId = "another_user"
                val anotherToken = "token-for-another"

                every {
                    signupService.process(SignupCommand(anotherLoginId, RAW_PASSWORD))
                } returns AuthToken(anotherToken)

                mockMvc.post("/v1/auth/signup") {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(
                        mapOf("loginId" to anotherLoginId, "password" to RAW_PASSWORD)
                    )
                }
                    .andExpect {
                        status { isCreated() }
                        jsonPath("$.resultType") { value("SUCCESS") }
                        header { string("Set-Cookie", org.hamcrest.Matchers.containsString("accessToken=$anotherToken")) }
                    }

                verify(exactly = 1) {
                    signupService.process(SignupCommand(anotherLoginId, RAW_PASSWORD))
                }
            }
        }

        `when`("중복된 loginId로 요청하면") {
            then("409와 DUPLICATED_LOGIN_ID를 반환한다") {
                every {
                    signupService.process(SignupCommand(LOGIN_ID, RAW_PASSWORD))
                } throws DuplicatedLoginIdException("이미 사용 중인 아이디입니다. loginId: $LOGIN_ID")

                mockMvc.post("/v1/auth/signup") {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(
                        mapOf("loginId" to LOGIN_ID, "password" to RAW_PASSWORD)
                    )
                }
                    .andExpect {
                        status { isConflict() }
                        jsonPath("$.resultType") { value("ERROR") }
                        jsonPath("$.errorMessage.code") { value("DUPLICATED_LOGIN_ID") }
                        jsonPath("$.errorMessage.message") { value("이미 사용 중인 아이디입니다.") }
                    }
            }
        }

        `when`("요청 본문이 비어 있으면") {
            then("400 에러를 반환한다") {
                mockMvc.post("/v1/auth/signup") {
                    contentType = MediaType.APPLICATION_JSON
                    content = ""
                }
                    .andExpect {
                        status { isBadRequest() }
                    }

                verify(exactly = 0) { signupService.process(any()) }
            }
        }

        `when`("Content-Type 없이 요청하면") {
            then("415 에러를 반환한다") {
                mockMvc.post("/v1/auth/signup") {
                    content = objectMapper.writeValueAsString(
                        mapOf("loginId" to LOGIN_ID, "password" to RAW_PASSWORD)
                    )
                }
                    .andExpect {
                        status { isUnsupportedMediaType() }
                    }

                verify(exactly = 0) { signupService.process(any()) }
            }
        }
    }
}) {
    companion object {
        private const val LOGIN_ID = "test_user"
        private const val RAW_PASSWORD = "password123"
        private const val ACCESS_TOKEN = "jwt-access-token"
    }
}
