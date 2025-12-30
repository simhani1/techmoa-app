package site.techmoa.app.core.response

import site.techmoa.app.core.error.ErrorMessage
import site.techmoa.app.core.error.ErrorType

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

        fun <S> error(error: ErrorType, errorData: Any? = null): ApiResponse<S> {
            return ApiResponse(ResultType.ERROR, null, ErrorMessage(error, errorData))
        }
    }
}