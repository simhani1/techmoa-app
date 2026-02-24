package site.techmoa.application.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import site.techmoa.application.port.WebhookPort
import site.techmoa.application.usecase.SaveWebhookUseCase
import site.techmoa.domain.exception.DuplicatedWebhookException
import site.techmoa.domain.exception.ErrorCode
import site.techmoa.domain.model.Webhook
import site.techmoa.domain.model.WebhookPlatform

class WebhookServiceTest : BehaviorSpec({
    given("웹훅을 등록할 때") {
        `when`("이미 등록된 웹훅인 경우") {
            val webhookPort = mockk<WebhookPort>()
            val saveWebhookUseCase = SaveWebhookUseCase(webhookPort)
            val webhookService = WebhookService(saveWebhookUseCase)
            every { webhookPort.existsBy(DISCORD, WEBHOOK_URL) } returns true

            then("DuplicatedWebhookException이 발생하고 저장은 수행되지 않는다") {
                val exception = shouldThrow<DuplicatedWebhookException> {
                    webhookService.save(DISCORD, WEBHOOK_URL)
                }

                exception.errorCode shouldBe ErrorCode.DUPLICATED_WEBHOOK
                exception.message shouldBe "Webhook already exists. platform=DISCORD, url=$WEBHOOK_URL"
                verify(exactly = 1) { webhookPort.existsBy(DISCORD, WEBHOOK_URL) }
                verify(exactly = 0) { webhookPort.save(any()) }
            }
        }

        `when`("플랫폼과 URL로 저장을 요청하면") {
            val webhookPort = mockk<WebhookPort>()
            val saveWebhookUseCase = SaveWebhookUseCase(webhookPort)
            val webhookService = WebhookService(saveWebhookUseCase)
            val savedWebhook = Webhook.of(
                id = SAVED_WEBHOOK_ID,
                url = WEBHOOK_URL,
                platform = DISCORD,
            )
            every { webhookPort.existsBy(DISCORD, WEBHOOK_URL) } returns false
            every { webhookPort.save(any()) } returns savedWebhook

            then("정상 저장된 웹훅 정보를 반환한다") {
                val result = webhookService.save(DISCORD, WEBHOOK_URL)
                result.id shouldBe SAVED_WEBHOOK_ID
                result.platform shouldBe DISCORD
                result.url shouldBe WEBHOOK_URL
                verify(exactly = 1) { webhookPort.existsBy(DISCORD, WEBHOOK_URL) }
                verify(exactly = 1) { webhookPort.save(any()) }
            }
        }
    }
}) {
    companion object {
        private val DISCORD = WebhookPlatform.DISCORD
        private const val WEBHOOK_URL = "https://discord.com/api/webhooks"
        private const val SAVED_WEBHOOK_ID = 1L
    }
}
