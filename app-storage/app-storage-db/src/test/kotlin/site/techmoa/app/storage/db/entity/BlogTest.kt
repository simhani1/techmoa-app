package site.techmoa.app.storage.db.entity

import com.navercorp.fixturemonkey.FixtureMonkey
import com.navercorp.fixturemonkey.kotlin.KotlinPlugin
import com.navercorp.fixturemonkey.kotlin.giveMeBuilder
import com.navercorp.fixturemonkey.kotlin.instantiator.instantiateBy
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class BlogTest : DescribeSpec({

    lateinit var blog: Blog

    val fixtureMonkey = FixtureMonkey.builder()
        .plugin(KotlinPlugin())
        .build()

    beforeTest {
        blog = fixtureMonkey.giveMeBuilder<Blog>()
            .instantiateBy {
                factory<Blog>("of") {
                    parameter<String>("link")
                    parameter<String>("logoUrl")
                    parameter<String>("rssLink")
                }
            }
            .set("link", "http://link.com")
            .set("logoUrl", "http://logoUrl.com")
            .set("rssLink", "http://rssLink.com")
            .sample()
    }

    describe("Blog의") {
        context("of()를 호출하면") {
            it("ACTIVE 상태의 Blog를 생성한다.") {
                blog.status.shouldBe(OperationStatus.ACTIVE)
            }
        }
        context("delete()를 호출하면") {
            it("DELETED 상태로 변경한다.") {
                blog.delete()
                blog.status.shouldBe(OperationStatus.DELETED)
            }
        }
        context("pause()를 호출하면") {
            it("PAUSED 상태로 변경한다.") {
                blog.pause()
                blog.status.shouldBe(OperationStatus.PAUSED)
            }
        }
    }
}){
}