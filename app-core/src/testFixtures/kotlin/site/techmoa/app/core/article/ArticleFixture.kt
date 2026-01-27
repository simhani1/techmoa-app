package site.techmoa.app.core.article

import site.techmoa.app.core.article.domain.Article

class ArticleFixture {

    companion object {
        @JvmStatic
        fun giveMeOne(idx: Int, blogId: Int): Article {
            return Article.of(
                blogId = blogId.toLong(),
                title = "title-$blogId-$idx",
                link = "https://blog$blogId.com/$idx",
                guid = "guid-$blogId-$idx",
                pubDate = idx.toLong()
            )
        }
    }
}
