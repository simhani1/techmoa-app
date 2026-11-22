package site.techmoa.infrastructure.rest

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import site.techmoa.domain.event.ArticleNotification
import site.techmoa.domain.event.WebhookRateLimited

class DiscordWebhookSenderTest : BehaviorSpec({
    val article = ArticleNotification(1, "제목", "https://example.org/article", 0, "블로그", "https://discord.com/api/webhooks/123/test-token")

    Given("등록된 목적지로 전송할 때") {
        Then("허용되지 않은 주소는 HTTP 호출 전에 거부한다") {
            val builder = WebClient.builder().exchangeFunction { error("HTTP 요청이 발생했습니다") }
            val sender = DiscordWebhookSender(builder, jacksonObjectMapper())
            for (url in listOf("https://example.org/api/webhooks/123/token",
                "http://discord.com/api/webhooks/123/token", "https://discord.com/api/other")) {
                shouldThrow<site.techmoa.domain.exception.InvalidWebhookUrlException> {
                    sender.publish(article.copy(webhookUrl = url))
                }
            }
        }

        Then("리다이렉트 응답은 성공 처리하지 않는다") {
            val response = ClientResponse.create(HttpStatus.FOUND).build()
            val builder = WebClient.builder().exchangeFunction { Mono.just(response) }
            shouldThrow<IllegalStateException> {
                DiscordWebhookSender(builder, jacksonObjectMapper()).publish(article)
            }
        }
    }

    Given("웹훅 응답이 429일 때") {
        fun send(response: ClientResponse): WebhookRateLimited {
            val builder = WebClient.builder().exchangeFunction { Mono.just(response) }
            return shouldThrow<WebhookRateLimited> {
                DiscordWebhookSender(builder, jacksonObjectMapper()).publish(article)
            }
        }

        Then("Retry-After에 지정된 소수점 이하 시간까지 해당 웹훅을 대기시킨다") {
            val limit = send(ClientResponse.create(HttpStatus.TOO_MANY_REQUESTS)
                .header("Retry-After", "1.25").build())
            limit.delayMillis shouldBe 1500
            limit.global shouldBe false
        }

        Then("헤더와 본문 중 긴 시간에 20%를 더한다") {
            val limit = send(ClientResponse.create(HttpStatus.TOO_MANY_REQUESTS)
                .header("Retry-After", "65")
                .header("Content-Type", "application/json")
                .body("""{"retry_after":64.57,"global":false}""").build())
            limit.delayMillis shouldBe 78000
        }

        Then("본문으로 전달된 전역 제한과 재시도 시간을 읽는다") {
            val limit = send(ClientResponse.create(HttpStatus.TOO_MANY_REQUESTS)
                .header("Content-Type", "application/json")
                .body("""{"retry_after":2.5,"global":true}""").build())
            limit.delayMillis shouldBe 3000
            limit.global shouldBe true
        }
    }
})
