package site.techmoa.app.fixture.blog

import com.navercorp.fixturemonkey.FixtureMonkey
import com.navercorp.fixturemonkey.kotlin.KotlinPlugin
import com.navercorp.fixturemonkey.kotlin.giveMeBuilder
import com.navercorp.fixturemonkey.kotlin.instantiator.instantiateBy
import site.techmoa.app.blog.domain.Blog

class BlogFixture {

    companion object {

        fun giveMe(cnt: Int = 0): List<Blog> {
            return List(cnt) { index -> default((index + 1).toLong()) }
        }

        private fun default(blogId: Long): Blog {
            val fixtureMonkey = FixtureMonkey.builder().plugin(KotlinPlugin()).build()

            val blog = fixtureMonkey.giveMeBuilder<Blog>()
                .instantiateBy {
                    constructor<Blog> {
                        parameter<Long>("id")
                        parameter<String>()
                    }
                }
                .set("id", blogId)
                .sample()

            return blog
        }
    }
}