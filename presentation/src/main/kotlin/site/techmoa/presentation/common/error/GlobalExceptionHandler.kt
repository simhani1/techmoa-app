package site.techmoa.presentation.common.error

import org.slf4j.LoggerFactory
import org.springframework.boot.logging.LogLevel
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import site.techmoa.domain.exception.DomainException
import site.techmoa.presentation.common.template.ApiResponse

@RestControllerAdvice
class GlobalExceptionHandler {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @ExceptionHandler(DomainException::class)
    fun handleDomainException(e: DomainException): ResponseEntity<ApiResponse<Any>> {
        val errorType = ErrorType.from(e.errorCode)

        when (errorType.logLevel) {
            LogLevel.INFO -> log.info("[{}] {}", errorType.code, e.message)
            LogLevel.WARN -> log.warn("[{}] {}", errorType.code, e.message)
            LogLevel.ERROR -> log.error("[{}] {}", errorType.code, e.message, e)
            else -> log.error("[{}] {}", errorType.code, e.message, e)
        }

        return ResponseEntity
            .status(errorType.status)
            .body(ApiResponse.error(errorType, e.message))
    }
}
