package site.techmoa.presentation.controller.request

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import site.techmoa.domain.exception.InvalidWebhookPlatformException
import site.techmoa.domain.exception.InvalidWebhookUrlException
import site.techmoa.domain.model.WebhookPlatform

class SaveWebhookRequestTest : BehaviorSpec({
    given("웹훅 저장 요청 파싱 상황에서") {
        `when`("플랫폼 문자열이 discord일 때 변환하면") {
            val request = SaveWebhookRequest(platform = "discord", url = "https://discord.com/api/webhooks/ok")

            then("DISCORD로 변환된다") {
                request.toPlatform() shouldBe WebhookPlatform.DISCORD
            }
        }

        `when`("지원하지 않는 플랫폼 문자열을 변환하면") {
            val request = SaveWebhookRequest(platform = "unknown", url = "https://discord.com/api/webhooks/ok")

            then("InvalidWebhookPlatformException이 발생한다") {
                val exception = shouldThrow<InvalidWebhookPlatformException> {
                    request.toPlatform()
                }
                exception.errorCode.name shouldBe "INVALID_WEBHOOK_PLATFORM"
            }
        }

        `when`("url 문자열을 정규화하면") {
            val request = SaveWebhookRequest(platform = "discord", url = "  https://discord.com/api/webhooks/ok  ")

            then("trim된 url이 반환된다") {
                request.normalizedUrl() shouldBe "https://discord.com/api/webhooks/ok"
            }
        }

        `when`("url 문자열이 비어있는 상태에서 정규화하면") {
            val request = SaveWebhookRequest(platform = "discord", url = "   ")

            then("InvalidWebhookUrlException이 발생한다") {
                val exception = shouldThrow<InvalidWebhookUrlException> {
                    request.normalizedUrl()
                }
                exception.errorCode.name shouldBe "INVALID_WEBHOOK_URL"
            }
        }
    }
})
