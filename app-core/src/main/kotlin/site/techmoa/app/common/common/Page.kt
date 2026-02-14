package site.techmoa.app.common.common

data class Page<T>(
    val data: List<T>,
    val hasNext: Boolean,
    val nextCursor: Long? = null,
) {
}
