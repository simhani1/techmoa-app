package site.techmoa.presentation.common.template

import site.techmoa.presentation.common.error.ErrorMessage
import site.techmoa.presentation.common.error.ErrorType

class ApiResponse<T> private constructor(
    val resultType: ResultType,
    val data: T? = null,
    val errorMessage: ErrorMessage? = null
) {
    companion object {
        fun success(): ApiResponse<Any> {
            return ApiResponse(ResultType.SUCCESS, null, null)
        }

        fun <S> success(data: S): ApiResponse<S> {
            return ApiResponse(ResultType.SUCCESS, data, null)
        }

        fun <S> error(errorType: ErrorType): ApiResponse<S> {
            return ApiResponse(ResultType.ERROR, null, ErrorMessage(errorType))
        }
    }
}
