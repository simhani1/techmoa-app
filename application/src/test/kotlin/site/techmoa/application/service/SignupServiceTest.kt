package site.techmoa.application.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import site.techmoa.application.dto.AuthToken
import site.techmoa.application.dto.SignupCommand
import site.techmoa.application.port.AuthTokenPort
import site.techmoa.application.port.MemberPort
import site.techmoa.application.port.PasswordPort
import site.techmoa.domain.exception.DuplicatedLoginIdException
import site.techmoa.domain.exception.ErrorCode
import site.techmoa.domain.model.Member
import site.techmoa.domain.model.OauthProvider

class SignupServiceTest : BehaviorSpec({

    given("자체 회원가입 요청에서") {

        `when`("유효한 loginId와 password로 가입하면") {
            val memberPort = mockk<MemberPort>()
            val passwordPort = mockk<PasswordPort>()
            val authTokenPort = mockk<AuthTokenPort>()
            val signupService = SignupService(memberPort, passwordPort, authTokenPort)

            every { memberPort.existsByLoginId(LOGIN_ID) } returns false
            every { passwordPort.encode(RAW_PASSWORD) } returns ENCODED_PASSWORD
            every { memberPort.saveLocal(LOGIN_ID, ENCODED_PASSWORD) } returns localMember()
            every { authTokenPort.issue(MEMBER_ID) } returns AuthToken(ACCESS_TOKEN)

            then("토큰을 발급하여 반환한다") {
                val result = signupService.process(SignupCommand(LOGIN_ID, RAW_PASSWORD))

                result.accessToken shouldBe ACCESS_TOKEN

                verify(exactly = 1) { memberPort.existsByLoginId(LOGIN_ID) }
                verify(exactly = 1) { passwordPort.encode(RAW_PASSWORD) }
                verify(exactly = 1) { memberPort.saveLocal(LOGIN_ID, ENCODED_PASSWORD) }
                verify(exactly = 1) { authTokenPort.issue(MEMBER_ID) }
            }
        }

        `when`("다른 유효한 loginId로 가입하면") {
            val memberPort = mockk<MemberPort>()
            val passwordPort = mockk<PasswordPort>()
            val authTokenPort = mockk<AuthTokenPort>()
            val signupService = SignupService(memberPort, passwordPort, authTokenPort)

            val anotherLoginId = "another_user"
            val anotherMemberId = 99L
            val anotherToken = "token-for-another"

            every { memberPort.existsByLoginId(anotherLoginId) } returns false
            every { passwordPort.encode(RAW_PASSWORD) } returns ENCODED_PASSWORD
            every { memberPort.saveLocal(anotherLoginId, ENCODED_PASSWORD) } returns localMember(
                id = anotherMemberId,
                loginId = anotherLoginId,
            )
            every { authTokenPort.issue(anotherMemberId) } returns AuthToken(anotherToken)

            then("해당 회원의 토큰을 발급하여 반환한다") {
                val result = signupService.process(SignupCommand(anotherLoginId, RAW_PASSWORD))

                result.accessToken shouldBe anotherToken

                verify(exactly = 1) { memberPort.existsByLoginId(anotherLoginId) }
                verify(exactly = 1) { memberPort.saveLocal(anotherLoginId, ENCODED_PASSWORD) }
                verify(exactly = 1) { authTokenPort.issue(anotherMemberId) }
            }
        }

        `when`("이미 존재하는 loginId로 가입하면") {
            val memberPort = mockk<MemberPort>()
            val passwordPort = mockk<PasswordPort>()
            val authTokenPort = mockk<AuthTokenPort>()
            val signupService = SignupService(memberPort, passwordPort, authTokenPort)

            every { memberPort.existsByLoginId(LOGIN_ID) } returns true

            then("DuplicatedLoginIdException이 발생하고 저장은 수행되지 않는다") {
                val exception = shouldThrow<DuplicatedLoginIdException> {
                    signupService.process(SignupCommand(LOGIN_ID, RAW_PASSWORD))
                }

                exception.errorCode shouldBe ErrorCode.DUPLICATED_LOGIN_ID
                exception.message shouldBe "이미 사용 중인 아이디입니다. loginId: $LOGIN_ID"

                verify(exactly = 1) { memberPort.existsByLoginId(LOGIN_ID) }
                verify(exactly = 0) { passwordPort.encode(any()) }
                verify(exactly = 0) { memberPort.saveLocal(any(), any()) }
                verify(exactly = 0) { authTokenPort.issue(any()) }
            }
        }

        `when`("비밀번호 인코딩 후 회원 저장이 실패하면") {
            val memberPort = mockk<MemberPort>()
            val passwordPort = mockk<PasswordPort>()
            val authTokenPort = mockk<AuthTokenPort>()
            val signupService = SignupService(memberPort, passwordPort, authTokenPort)

            every { memberPort.existsByLoginId(LOGIN_ID) } returns false
            every { passwordPort.encode(RAW_PASSWORD) } returns ENCODED_PASSWORD
            every { memberPort.saveLocal(LOGIN_ID, ENCODED_PASSWORD) } throws RuntimeException("DB error")

            then("예외가 전파되고 토큰 발급은 수행되지 않는다") {
                val exception = shouldThrow<RuntimeException> {
                    signupService.process(SignupCommand(LOGIN_ID, RAW_PASSWORD))
                }

                exception.message shouldBe "DB error"

                verify(exactly = 1) { memberPort.existsByLoginId(LOGIN_ID) }
                verify(exactly = 1) { passwordPort.encode(RAW_PASSWORD) }
                verify(exactly = 1) { memberPort.saveLocal(LOGIN_ID, ENCODED_PASSWORD) }
                verify(exactly = 0) { authTokenPort.issue(any()) }
            }
        }
    }
}) {
    companion object {
        private const val LOGIN_ID = "test_user"
        private const val RAW_PASSWORD = "password123"
        private const val ENCODED_PASSWORD = "\$2a\$10\$encodedPasswordHash"
        private const val MEMBER_ID = 1L
        private const val ACCESS_TOKEN = "jwt-access-token"
    }
}

private fun localMember(
    id: Long = 1L,
    loginId: String = "test_user",
): Member = Member(
    id = id,
    email = "",
    provider = OauthProvider.LOCAL,
    subject = loginId,
)
