package site.techmoa.batch.rss.usecase

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import org.springframework.context.ApplicationEvent
import org.springframework.context.ApplicationEventPublisher
import site.techmoa.batch.rss.domain.event.NewArticlesCollectedEvent
import site.techmoa.batch.rss.domain.event.OutboxStatus
import site.techmoa.batch.rss.domain.model.Article

class PublishEventUseCaseTest : BehaviorSpec({

    val eventPublisher = MockEventPublisher()
    val eventUseCase = PublishEventUseCase(eventPublisher)

    afterEach() {
        eventPublisher.clear()
    }

    given("새로운 아티클 목록이 비어있을 때") {
        val articles = emptyList<Article>()

        `when`("이벤트를 발행하면") {
            then("Spring 내부 이벤트를 발행하지 않는다") {
                eventUseCase.publish(articles)
                eventPublisher.events.size shouldBe 0
            }
        }
    }

    given("새로운 아티클 목록이 있을 때") {
        val articles = listOf<Article>(
            Article.of(
                blogId = 1L,
                blogName = "기술 블로그",
                guid = "guid-1",
                title = "첫 글",
                link = "https://example.com/1",
                pubDate = 1709251200000L // 2024-03-01T00:00:00Z
            ),
            Article.of(
                blogId = 2L,
                blogName = "테크모아",
                guid = "guid-2",
                title = "둘째 글",
                link = "https://example.com/2",
                pubDate = 1711929600000L // 2024-04-01T00:00:00Z
            )
        )

        `when`("이벤트를 발행하면") {
            then("NewArticlesCollectedEvent가 발행된다") {
                eventUseCase.publish(articles)
                eventPublisher.events.size shouldBe 1

                val event = eventPublisher.events.first() as NewArticlesCollectedEvent
                event.size() shouldBe 2

                event.events[0].blogId shouldBe 1L
                event.events[0].guid shouldBe "guid-1"
                event.events[0].idempotencyKey shouldBe "202403:1:guid-1"
                event.events[0].status shouldBe OutboxStatus.PENDING

                event.events[1].blogId shouldBe 2L
                event.events[1].guid shouldBe "guid-2"
                event.events[1].idempotencyKey shouldBe "202404:2:guid-2"
                event.events[1].status shouldBe OutboxStatus.PENDING
            }
        }
    }
}) {
    private class MockEventPublisher : ApplicationEventPublisher {
        val events = mutableListOf<Any>()

        override fun publishEvent(event: ApplicationEvent) {
            events += event
        }

        override fun publishEvent(event: Any) {
            events += event
        }

        fun clear() {
            events.clear()
        }
    }
}
