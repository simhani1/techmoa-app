package site.techmoa.worker.scheduler.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import site.techmoa.domain.event.OutboxMessages.NewArticlesOutboxMessage.OutboxPayload
import site.techmoa.domain.event.WebhookRateLimited
import site.techmoa.domain.event.WebhookSender
import site.techmoa.worker.scheduler.repository.OutboxRepository
import site.techmoa.worker.scheduler.repository.OutboxRepository.Delivery

class OutboxDispatchServiceTest : BehaviorSpec({
    for (global in listOf(false, true)) {
        val scope = if (global) "전역" else "개별"
        Given("두 번째 글에서 $scope 제한이 발생하면") {
            Then("성공한 첫 글만 기록하고 제한 범위에 따라 다음 웹훅을 처리한다") {
                val mapper = jacksonObjectMapper()
                val repository = mockk<OutboxRepository>(relaxUnitFun = true)
                val sender = mockk<WebhookSender>(relaxUnitFun = true)
                fun article(id: Long) = OutboxPayload(id, "title", "link", 0, "blog", "url")
                fun delivery(id: Long) = Delivery(id, mapper.writeValueAsString(article(id)))
                val limit = WebhookRateLimited(1500, global)
                every { repository.readyWebhooks() } returns listOf(1L, 2L)
                every { repository.pending(1) } returns listOf(delivery(1), delivery(2), delivery(3))
                every { repository.pending(2) } returnsMany listOf(listOf(delivery(4)), emptyList())
                every { sender.publish(article(2)) } throws limit

                OutboxDispatchService(repository, sender, mapper).dispatchPending()

                verify(exactly = 1) { repository.saveResults(1, listOf(1L), emptyMap(), limit) }
                verify(exactly = 0) { sender.publish(article(3)) }
                verify(exactly = if (global) 0 else 1) { sender.publish(article(4)) }
            }
        }
    }
})
