package site.techmoa.app.core.interceptor

class InvalidAuthorizationException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)
