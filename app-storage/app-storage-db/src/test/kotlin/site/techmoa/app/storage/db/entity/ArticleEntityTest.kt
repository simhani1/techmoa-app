package site.techmoa.app.storage.db.entity

import com.navercorp.fixturemonkey.FixtureMonkey
import com.navercorp.fixturemonkey.kotlin.KotlinPlugin
import com.navercorp.fixturemonkey.kotlin.giveMeBuilder
import com.navercorp.fixturemonkey.kotlin.instantiator.instantiateBy
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import kotlin.random.Random

class ArticleEntityTest : DescribeSpec({

    lateinit var article: ArticleEntity

    val fixtureMonkey = FixtureMonkey.builder()
        .plugin(KotlinPlugin())
        .build()

    beforeTest {
        article = fixtureMonkey.giveMeBuilder<ArticleEntity>()
            .instantiateBy {
                factory<ArticleEntity>("of")
            }
            .sample()
    }

    describe("Article의") {
        context("기본 조회수는") {
            it("0이다.") {
                article.getViews().shouldBe(0)
            }
        }
        context("increaseViews()를 호출하면") {
            it("조회수가 1 증가한다") {
                article.increaseViews()
                article.getViews().shouldBe(1)
            }
        }
        context("increaseViews(amount)를 호출하면") {
            it("조회수가 amount 증가한다") {
                val amount = Random.nextInt(1, Int.MAX_VALUE)
                article.increaseViews(amount)
                article.getViews().shouldBe(amount)
            }
        }
    }
}) {
}