package site.techmoa.app.common.interceptor

class InvalidAuthorizationException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)
