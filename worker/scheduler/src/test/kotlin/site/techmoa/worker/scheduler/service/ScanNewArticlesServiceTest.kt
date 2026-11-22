package site.techmoa.worker.scheduler.service

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import site.techmoa.domain.event.ArticleNotification
import site.techmoa.worker.scheduler.dto.LastScannedArticleDto
import site.techmoa.worker.scheduler.dto.NewArticleDto
import site.techmoa.worker.scheduler.repository.*
import site.techmoa.worker.scheduler.usecase.LastScannedArticleUseCase
import java.time.LocalDateTime

class ScanNewArticlesServiceTest : BehaviorSpec({
    Given("같은 글을 받을 웹훅의 등록 시점이 다를 때") {
        Then("등록 이후 저장된 글만 목적지별로 기록하고 확인 위치를 갱신한다") {
            val start = LocalDateTime.of(2026, 1, 1, 10, 0)
            val articles = (0L..2L).map { id ->
                NewArticleDto(id, start.plusNanos(id * 1000), "제목", "/article", 0, 1, "블로그")
            }
            val cursor = mockk<LastScannedArticleUseCase>(relaxUnitFun = true)
            val articleRepository = mockk<ArticleRepository>()
            val webhookRepository = mockk<WebhookRepository>()
            val outbox = mockk<OutboxRepository>(relaxUnitFun = true)
            every { cursor.getLastScannedArticle() } returns LastScannedArticleDto()
            every { articleRepository.findByIdGreaterThan(any()) } returns articles
            every { webhookRepository.getValidWebhook() } returns listOf(
                RegisteredWebhook(10, "https://example.org/a", start),
                RegisteredWebhook(20, "https://example.org/b", start.plusNanos(1000)),
            )

            ScanNewArticlesService(cursor, outbox, articleRepository, webhookRepository).execute()

            val firstWebhook = slot<List<ArticleNotification>>()
            val secondWebhook = slot<List<ArticleNotification>>()
            verifyOrder {
                outbox.enqueue(10, capture(firstWebhook))
                outbox.enqueue(20, capture(secondWebhook))
                cursor.sync(2)
            }
            firstWebhook.captured.map { it.articleId } shouldBe listOf(1L, 2L)
            secondWebhook.captured.map { it.articleId } shouldBe listOf(2L)
        }
    }
})
