package site.techmoa.application.common

data class OffsetLimit(
    val offset: Int = 0,
    val limit: Int,
) {
}