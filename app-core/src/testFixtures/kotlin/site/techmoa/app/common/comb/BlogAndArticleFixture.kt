package site.techmoa.app.common.comb

import site.techmoa.app.common.article.Article
import site.techmoa.app.common.article.ArticleFixture
import site.techmoa.app.common.blog.Blog
import site.techmoa.app.common.blog.BlogFixture

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
