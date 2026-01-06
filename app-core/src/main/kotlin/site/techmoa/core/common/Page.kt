package site.techmoa.core.common

data class Page<T>(
    val data: List<T>,
    val hasNext: Boolean,
    val nextCursor: Long? = null,
) {
}
