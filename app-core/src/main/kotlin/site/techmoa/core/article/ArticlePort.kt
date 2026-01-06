package site.techmoa.core.article

interface ArticlePort {
    fun findById(id: Long): Article?
    fun save(article: Article)
}