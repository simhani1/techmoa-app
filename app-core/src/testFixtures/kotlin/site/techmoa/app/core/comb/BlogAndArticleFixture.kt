package site.techmoa.app.core.comb

import site.techmoa.app.core.article.ArticleFixture
import site.techmoa.app.core.article.domain.Article
import site.techmoa.app.core.blog.Blog
import site.techmoa.app.core.blog.BlogFixture

class BlogAndArticleFixture {

    companion object {
        @JvmStatic
        fun giveMe(blogCnt: Int, articleCnt: Int): Map<Blog, List<Article>> {
            val map = HashMap<Blog, List<Article>>(blogCnt)
            for (blogIndex in 1..blogCnt) {
                val blog = BlogFixture.giveMeOne(blogIndex)
                val articles = (1..articleCnt).map { articleIndex ->
                    ArticleFixture.giveMeOne(articleIndex, blogIndex)
                }
                map[blog] = articles
            }
            return map
        }
    }
}
