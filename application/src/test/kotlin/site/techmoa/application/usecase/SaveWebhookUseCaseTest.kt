package site.techmoa.application.usecase

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import site.techmoa.application.port.WebhookPort
import site.techmoa.domain.exception.DuplicatedWebhookException
import site.techmoa.domain.model.Webhook
import site.techmoa.domain.model.WebhookPlatform
import site.techmoa.domain.model.WebhookValidity

class SaveWebhookUseCaseTest : BehaviorSpec({
    given("새로운 webhook url 저장 상황에서") {
        val webhookPort = mockk<WebhookPort>()
        val useCase = SaveWebhookUseCase(webhookPort)
        val captured = slot<Webhook>()

        every { webhookPort.existsBy(WebhookPlatform.DISCORD, "https://discord.com/api/webhooks/123") } returns false
        every { webhookPort.save(capture(captured)) } answers {
            captured.captured.copy(id = 1L, validity = WebhookValidity.VALID)
        }

        `when`("중복이 아닌 url을 저장하면") {
            val result = useCase.save(WebhookPlatform.DISCORD, "https://discord.com/api/webhooks/123")

            then("저장 후 생성된 webhook을 반환한다") {
                result.id shouldBe 1L
                result.platform shouldBe WebhookPlatform.DISCORD
                result.url shouldBe "https://discord.com/api/webhooks/123"
                verify(exactly = 1) { webhookPort.save(any()) }
            }
        }
    }

    given("이미 등록된 webhook url 저장 상황에서") {
        val webhookPort = mockk<WebhookPort>()
        val useCase = SaveWebhookUseCase(webhookPort)

        every { webhookPort.existsBy(WebhookPlatform.DISCORD, "https://discord.com/api/webhooks/dup") } returns true

        `when`("동일한 url을 저장하면") {
            then("DuplicatedWebhookException을 던진다") {
                val exception = shouldThrow<DuplicatedWebhookException> {
                    useCase.save(WebhookPlatform.DISCORD, "https://discord.com/api/webhooks/dup")
                }
                exception.errorCode.name shouldBe "DUPLICATED_WEBHOOK"
                verify(exactly = 0) { webhookPort.save(any()) }
            }
        }
    }
})
