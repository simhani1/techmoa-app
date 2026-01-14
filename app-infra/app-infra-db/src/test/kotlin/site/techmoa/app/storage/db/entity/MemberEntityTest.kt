package site.techmoa.app.storage.db.entity

import com.navercorp.fixturemonkey.FixtureMonkey
import com.navercorp.fixturemonkey.kotlin.KotlinPlugin
import com.navercorp.fixturemonkey.kotlin.giveMeBuilder
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldNotBeBlank

class MemberEntityTest: DescribeSpec({

    val fixtureMonkey = FixtureMonkey.builder()
        .plugin(KotlinPlugin())
        .build()

    describe("지원하는 소셜 로그인 플랫폼 중") {
        context("카카오로 가입하면") {

            val member = fixtureMonkey.giveMeBuilder<MemberEntity>()
                .set("provider", MemberEntity.OAuthProvider.KAKAO)
                .set("providerUserId", "kakao_12345")
                .sample()

            it("provider는 KAKAO다") {
                member.provider.shouldBe(MemberEntity.OAuthProvider.KAKAO)
            }
            it("providerUserId는 필수다") {
                member.providerUserId.shouldNotBeBlank()
            }
        }
    }
}) {
}
