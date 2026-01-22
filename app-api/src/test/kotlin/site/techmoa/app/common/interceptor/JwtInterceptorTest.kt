package site.techmoa.app.common.interceptor

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.startWith
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.web.method.HandlerMethod
import site.techmoa.app.common.annotation.AuthOptional
import site.techmoa.app.common.annotation.AuthRequired
import site.techmoa.app.common.auth.jwt.JwtTokenProvider
import site.techmoa.app.common.context.MemberContextHolder
import site.techmoa.app.common.context.Passport

class JwtInterceptorTest {

    private val jwtTokenProvider = mockk<JwtTokenProvider>()
    private val interceptor = JwtInterceptor(jwtTokenProvider)

    @AfterEach
    fun tearDown() {
        MemberContextHolder.clear()
    }

    @Test
    fun `OPTIONAL 정책은 Authorization 헤더가 없으면 Guest를 등록한다`() {
        // GIVEN
        val request = MockHttpServletRequest()
        val response = MockHttpServletResponse()
        val handler = createHandlerMethod(AuthOptional())

        // WHEN
        val result = interceptor.preHandle(request, response, handler)

        // THEN
        result shouldBe true
        MemberContextHolder.getPassport() shouldBeSameInstanceAs Passport.Guest
    }

    @Test
    fun `유효한 Bearer 토큰이 전달되면 Member를 등록한다`() {
        // GIVEN
        val memberId = 123L
        val token = "valid-token"
        every { jwtTokenProvider.parse(token) } returns memberId

        val authHeader = "Bearer $token"
        val request = MockHttpServletRequest().apply {
            addHeader("Authorization", authHeader)
        }
        val response = MockHttpServletResponse()
        val handler = createHandlerMethod(AuthRequired())

        // WHEN
        val result = interceptor.preHandle(request, response, handler)

        // THEN
        result shouldBe true
        MemberContextHolder.getPassport() shouldBe Passport.Member(memberId)
    }

    @Test
    fun `Bearer 접두사가 없으면 InvalidAuthorizationException 예외를 던진다`() {
        // GIVEN
        val request = MockHttpServletRequest().apply {
            addHeader("Authorization", "InvalidToken")
        }
        val response = MockHttpServletResponse()
        val handler = createHandlerMethod(AuthRequired())

        // WHEN/THEN
        val exception = shouldThrow<InvalidAuthorizationException> {
            interceptor.preHandle(request, response, handler)
        }
        exception.message should startWith("Invalid Authorization header")
    }

    @Test
    fun `Bearer 접두사만 있고 토큰이 비어있으면 예외를 던진다`() {
        // GIVEN
        val request = MockHttpServletRequest().apply {
            addHeader("Authorization", "Bearer   ")
        }
        val response = MockHttpServletResponse()
        val handler = createHandlerMethod(AuthRequired())

        // WHEN/THEN
        val exception = shouldThrow<InvalidAuthorizationException> {
            interceptor.preHandle(request, response, handler)
        }
        exception.message should startWith("Missing bearer token")
    }

    private fun createHandlerMethod(annotation: Annotation): HandlerMethod {
        val handlerMethod = mockk<HandlerMethod>()
        every { handlerMethod.hasMethodAnnotation(AuthRequired::class.java) } returns (annotation is AuthRequired)
        every { handlerMethod.hasMethodAnnotation(AuthOptional::class.java) } returns (annotation is AuthOptional)
        return handlerMethod
    }
}
