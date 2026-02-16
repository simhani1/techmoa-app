package site.techmoa.domain.exception

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class WebhookExceptionTest : BehaviorSpec({
    given("웹훅 예외 생성 규칙을 확인할 때") {
        `when`("DuplicatedWebhookException을 만들면") {
            val exception = DuplicatedWebhookException("duplicated")

            then("ErrorCode는 DUPLICATED_WEBHOOK이다") {
                exception.errorCode shouldBe ErrorCode.DUPLICATED_WEBHOOK
            }
        }

        `when`("InvalidWebhookPlatformException을 만들면") {
            val exception = InvalidWebhookPlatformException("invalid platform")

            then("ErrorCode는 INVALID_WEBHOOK_PLATFORM이다") {
                exception.errorCode shouldBe ErrorCode.INVALID_WEBHOOK_PLATFORM
            }
        }

        `when`("InvalidWebhookUrlException을 만들면") {
            val exception = InvalidWebhookUrlException("invalid url")

            then("ErrorCode는 INVALID_WEBHOOK_URL이다") {
                exception.errorCode shouldBe ErrorCode.INVALID_WEBHOOK_URL
            }
        }
    }
})
