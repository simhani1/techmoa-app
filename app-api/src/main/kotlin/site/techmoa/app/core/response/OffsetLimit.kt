package site.techmoa.app.core.response

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable

data class OffsetLimit(
    val offset: Int = 0,
    val limit: Int,
) {
    fun toPageable(): Pageable {
        return PageRequest.of(offset / limit, limit)
    }
}