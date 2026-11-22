package site.techmoa.worker.scheduler.usecase

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import org.springframework.context.ApplicationEventPublisher
import site.techmoa.domain.event.OutboxMessages
import site.techmoa.worker.scheduler.dto.NewArticleDto
import site.techmoa.worker.scheduler.repository.RegisteredWebhook
import java.time.LocalDateTime

class NewArticlesEventUseCaseTest : BehaviorSpec({
    Given("같은 글을 받을 웹훅의 등록 시점이 다를 때") {
        Then("등록 이후 DB에 처음 저장된 글만 목적지별로 기록한다") {
            val start = LocalDateTime.of(2026, 1, 1, 10, 0)
            val articles = (0L..2L).map { id ->
                NewArticleDto(id, start.plusNanos(id * 1000), "제목", "/article", 0, 1, "블로그")
            }
            val webhooks = listOf(
                RegisteredWebhook(10, "https://example.org/a", start),
                RegisteredWebhook(20, "https://example.org/b", start.plusNanos(1000)),
            )
            val events = mutableListOf<OutboxMessages>()
            val publisher = ApplicationEventPublisher { events.add(it as OutboxMessages) }
            NewArticlesEventUseCase(jacksonObjectMapper(), publisher).publish(articles, webhooks)

            events.single().messages.map { it.webhookId to it.aggregateId } shouldBe
                listOf(10L to 1L, 10L to 2L, 20L to 2L)
        }
    }
})
