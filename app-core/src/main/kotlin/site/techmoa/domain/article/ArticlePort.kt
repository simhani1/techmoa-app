package site.techmoa.domain.article

interface ArticlePort {
    fun findById(id: Long): Article?
    fun save(article: Article)
}