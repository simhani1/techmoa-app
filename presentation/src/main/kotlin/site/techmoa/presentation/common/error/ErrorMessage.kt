package site.techmoa.presentation.common.error

data class ErrorMessage private constructor(
    val code: String,
    val message: String,
) {
    constructor(errorType: ErrorType) : this(
        code = errorType.code.name,
        message = errorType.message,
    )
}
