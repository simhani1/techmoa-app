package site.techmoa.app.core

data class Page<T>(
    val data: List<T>,
    val hasNext: Boolean,
    val nextCursor: Long? = null,
) {
}
