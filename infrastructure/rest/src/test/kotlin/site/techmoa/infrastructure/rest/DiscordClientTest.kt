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

class DiscordClientTest : BehaviorSpec({
    val article = ArticleNotification(1, "제목", "https://example.org/article", 0, "블로그", "https://example.org/webhook")

    Given("웹훅 응답이 429일 때") {
        fun send(response: ClientResponse): WebhookRateLimited {
            val builder = WebClient.builder().exchangeFunction { Mono.just(response) }
            return shouldThrow<WebhookRateLimited> {
                DiscordClient(builder, jacksonObjectMapper()).publish(article)
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
