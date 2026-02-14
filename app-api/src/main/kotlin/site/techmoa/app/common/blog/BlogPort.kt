package site.techmoa.app.common.blog

interface BlogPort {
    fun save(blog: Blog)
    fun findAllBy(ids: List<Long>): List<Blog>
    fun findAllBy(status: BlogStatus): List<Blog>
}