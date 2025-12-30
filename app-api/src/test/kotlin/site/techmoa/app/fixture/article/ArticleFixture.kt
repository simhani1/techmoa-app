package site.techmoa.app.fixture.article

import com.navercorp.fixturemonkey.FixtureMonkey
import com.navercorp.fixturemonkey.kotlin.KotlinPlugin
import com.navercorp.fixturemonkey.kotlin.giveMeBuilder
import com.navercorp.fixturemonkey.kotlin.instantiator.instantiateBy
import site.techmoa.app.article.domain.Article

class ArticleFixture {

    companion object {

        fun giveMe(cnt: Int = 0, blogId: Long): List<Article> {
            return List(cnt) { default(blogId) }
        }

        private fun default(blogId: Long): Article {
            val fixtureMonkey = FixtureMonkey.builder().plugin(KotlinPlugin()).build()

            val article = fixtureMonkey.giveMeBuilder<Article>()
                .instantiateBy {
                    constructor<Article> {
                        parameter<Long>("id")
                        parameter<Long>("blogId")
                    }
                }
                .set("blogId", blogId)
                .sample()

            return article
        }
    }
}
