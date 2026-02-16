package site.techmoa.presentation.controller

import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import site.techmoa.application.service.WebhookService
import site.techmoa.domain.exception.DuplicatedWebhookException
import site.techmoa.domain.model.Webhook
import site.techmoa.domain.model.WebhookPlatform
import site.techmoa.domain.model.WebhookValidity
import site.techmoa.presentation.common.error.GlobalExceptionHandler

class WebhookControllerV1Test : BehaviorSpec({
    val objectMapper = ObjectMapper()
    val webhookService = mockk<WebhookService>()
    val controller = WebhookControllerV1(webhookService)
    val mockMvc: MockMvc = MockMvcBuilders
        .standaloneSetup(controller)
        .setControllerAdvice(GlobalExceptionHandler())
        .build()

    beforeTest {
        clearMocks(webhookService)
    }

    given("POST /v1/webhooks 요청 처리 상황에서") {
        `when`("정상 요청을 보내면") {
            then("200과 webhookId를 반환한다") {
                every {
                    webhookService.save(WebhookPlatform.DISCORD, "https://discord.com/api/webhooks/ok")
                } returns Webhook(
                    id = 100L,
                    url = "https://discord.com/api/webhooks/ok",
                    validity = WebhookValidity.VALID,
                    platform = WebhookPlatform.DISCORD,
                )

                mockMvc.post("/v1/webhooks") {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(
                        mapOf("platform" to "discord", "url" to "https://discord.com/api/webhooks/ok")
                    )
                }
                    .andExpect {
                        status { isOk() }
                        jsonPath("$.resultType") { value("SUCCESS") }
                        jsonPath("$.data.webhookId") { value(100) }
                    }

                verify(exactly = 1) {
                    webhookService.save(WebhookPlatform.DISCORD, "https://discord.com/api/webhooks/ok")
                }
            }
        }

        `when`("중복 webhook으로 요청하면") {
            then("400과 DUPLICATED_WEBHOOK을 반환한다") {
                every {
                    webhookService.save(WebhookPlatform.DISCORD, "https://discord.com/api/webhooks/dup")
                } throws DuplicatedWebhookException("duplicate")

                mockMvc.post("/v1/webhooks") {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(
                        mapOf("platform" to "discord", "url" to "https://discord.com/api/webhooks/dup")
                    )
                }
                    .andExpect {
                        status { isBadRequest() }
                        jsonPath("$.resultType") { value("ERROR") }
                        jsonPath("$.errorMessage.code") { value("DUPLICATED_WEBHOOK") }
                    }
            }
        }

        `when`("지원하지 않는 플랫폼으로 요청하면") {
            then("400과 INVALID_WEBHOOK_PLATFORM을 반환한다") {
                mockMvc.post("/v1/webhooks") {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(
                        mapOf("platform" to "not-supported", "url" to "https://discord.com/api/webhooks/ok")
                    )
                }
                    .andExpect {
                        status { isBadRequest() }
                        jsonPath("$.resultType") { value("ERROR") }
                        jsonPath("$.errorMessage.code") { value("INVALID_WEBHOOK_PLATFORM") }
                    }

                verify(exactly = 0) { webhookService.save(any(), any()) }
            }
        }

        `when`("url이 공백인 상태로 요청하면") {
            then("400과 INVALID_WEBHOOK_URL을 반환한다") {
                mockMvc.post("/v1/webhooks") {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(
                        mapOf("platform" to "discord", "url" to "   ")
                    )
                }
                    .andExpect {
                        status { isBadRequest() }
                        jsonPath("$.resultType") { value("ERROR") }
                        jsonPath("$.errorMessage.code") { value("INVALID_WEBHOOK_URL") }
                    }

                verify(exactly = 0) { webhookService.save(any(), any()) }
            }
        }
    }
})
