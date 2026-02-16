package site.techmoa.presentation.common.error

import org.springframework.boot.logging.LogLevel
import org.springframework.http.HttpStatus
import site.techmoa.domain.exception.ErrorCode

enum class ErrorType(
    val status: HttpStatus,
    val code: ErrorCode,
    val message: String,
    val logLevel: LogLevel
) {
    DEFAULT_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.INTERNAL_SERVER_ERROR, "알 수 없는 오류가 발생했습니다. 잠시 후 다시 시도해주세요.", LogLevel.ERROR),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, ErrorCode.BAD_REQUEST, "요청이 올바르지 않습니다.", LogLevel.INFO),
    NOT_FOUND_DATA(HttpStatus.NOT_FOUND, ErrorCode.NOT_FOUND_DATA, "해당 데이터를 찾을 수 없습니다.", LogLevel.INFO),

    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, ErrorCode.UNAUTHORIZED, "사용자 인증에 실패했습니다.", LogLevel.WARN),
    FORBIDDEN(HttpStatus.FORBIDDEN, ErrorCode.FORBIDDEN, "권한이 확인되지 않습니다.", LogLevel.WARN),

    KAKAO_CLIENT_ERROR(HttpStatus.BAD_REQUEST, ErrorCode.KAKAO_CLIENT_ERROR, "카카오 인증 요청이 올바르지 않습니다.", LogLevel.WARN),
    KAKAO_SERVER_ERROR(HttpStatus.BAD_GATEWAY, ErrorCode.KAKAO_SERVER_ERROR, "카카오 인증 서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.", LogLevel.ERROR),
    KID_NOT_MATCH(HttpStatus.UNAUTHORIZED, ErrorCode.KID_NOT_MATCH, "OIDC 토큰 검증에 실패했습니다.", LogLevel.WARN);

    companion object {
        fun from(errorCode: ErrorCode): ErrorType {
            return entries.firstOrNull { it.code == errorCode } ?: DEFAULT_ERROR
        }
    }
}
