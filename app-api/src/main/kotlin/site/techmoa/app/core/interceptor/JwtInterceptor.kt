package site.techmoa.app.core.interceptor

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor
import site.techmoa.app.core.annotation.AuthOptional
import site.techmoa.app.core.annotation.AuthPolicy
import site.techmoa.app.core.annotation.AuthRequired
import site.techmoa.app.core.auth.jwt.JwtTokenProvider
import site.techmoa.app.core.context.MemberContextHolder
import site.techmoa.app.core.context.Passport

@Component
class JwtInterceptor(
    private val jwtTokenProvider: JwtTokenProvider,
) : HandlerInterceptor {

    companion object {
        private const val AUTHORIZATION_HEADER = "Authorization"
        private const val BEARER_PREFIX = "Bearer "
    }

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val policy = resolvePolicy(handler)
        val authorization = request.getHeader(AUTHORIZATION_HEADER)

        if (authorization.isNullOrBlank()) {
            if (policy == AuthPolicy.REQUIRED) {
                throw InvalidAuthorizationException("Authorization required")
            }
            MemberContextHolder.setPassport(Passport.Guest)
            return true
        }

        handleAuthorization(authorization)
        return true
    }

    private fun handleAuthorization(authorization: String) {
        if (!authorization.startsWith(BEARER_PREFIX)) {
            throw InvalidAuthorizationException("Invalid Authorization header")
        }
        val token = authorization.removePrefix(BEARER_PREFIX).trim()
        if (token.isBlank()) {
            throw InvalidAuthorizationException("Missing bearer token")
        }
        val memberId = jwtTokenProvider.parse(token)
        MemberContextHolder.setPassport(Passport.Member(memberId))
    }

    override fun afterCompletion(request: HttpServletRequest, response: HttpServletResponse, handler: Any, ex: Exception?) {
        MemberContextHolder.clear()
    }

    private fun resolvePolicy(handler: Any): AuthPolicy {
        if (handler !is HandlerMethod) {
            throw RuntimeException("Handler method must be HandlerMethod")
        }
        return when {
            handler.hasMethodAnnotation(AuthRequired::class.java) -> AuthPolicy.REQUIRED
            handler.hasMethodAnnotation(AuthOptional::class.java) -> AuthPolicy.OPTIONAL
            else -> AuthPolicy.OPTIONAL
        }
    }
}
