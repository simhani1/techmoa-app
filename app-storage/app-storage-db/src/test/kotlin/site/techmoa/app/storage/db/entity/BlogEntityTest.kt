package site.techmoa.app.storage.db.entity

import com.navercorp.fixturemonkey.FixtureMonkey
import com.navercorp.fixturemonkey.kotlin.KotlinPlugin
import com.navercorp.fixturemonkey.kotlin.giveMeBuilder
import com.navercorp.fixturemonkey.kotlin.instantiator.instantiateBy
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class BlogEntityTest : DescribeSpec({

    lateinit var blog: BlogEntity

    val fixtureMonkey = FixtureMonkey.builder()
        .plugin(KotlinPlugin())
        .build()

    beforeTest {
        blog = fixtureMonkey.giveMeBuilder<BlogEntity>()
            .instantiateBy {
                factory<BlogEntity>("of") {
                    parameter<String>("link")
                    parameter<String>("name")
                    parameter<String>("logoUrl")
                    parameter<String>("rssLink")
                }
            }
            .sample()
    }

    describe("Blog의") {
        context("of()를 호출하면") {
            it("ACTIVE 상태의 Blog를 생성한다") {
                blog.status.shouldBe(BlogStatus.ACTIVE)
            }
        }
        context("delete()를 호출하면") {
            it("DELETED 상태로 변경한다") {
                blog.delete()
                blog.status.shouldBe(BlogStatus.DELETED)
            }
        }
        context("pause()를 호출하면") {
            it("PAUSED 상태로 변경한다") {
                blog.pause()
                blog.status.shouldBe(BlogStatus.PAUSED)
            }
        }
    }
}){
}