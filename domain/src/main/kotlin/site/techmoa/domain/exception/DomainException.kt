package site.techmoa.domain.exception

sealed class DomainException(
    open val errorCode: ErrorCode,
    override val message: String,
    override val cause: Throwable? = null,
) : RuntimeException(message, cause)

class NotFoundException(
    override val message: String,
    override val cause: Throwable? = null,
) : DomainException(ErrorCode.NOT_FOUND_DATA, message, cause)

class InvalidTokenException(
    override val message: String,
    override val cause: Throwable? = null,
) : DomainException(ErrorCode.BAD_REQUEST, message, cause)

class KakaoClientException(
    override val message: String,
    override val cause: Throwable? = null,
) : DomainException(ErrorCode.KAKAO_CLIENT_ERROR, message, cause)

class KakaoServerException(
    override val message: String,
    override val cause: Throwable? = null,
) : DomainException(ErrorCode.KAKAO_SERVER_ERROR, message, cause)

class KidNotMatchException(
    override val message: String,
    override val cause: Throwable? = null,
) : DomainException(ErrorCode.KID_NOT_MATCH, message, cause)