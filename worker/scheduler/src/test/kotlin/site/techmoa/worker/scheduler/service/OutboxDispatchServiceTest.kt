package site.techmoa.worker.scheduler.service

import site.techmoa.domain.model.WebhookPlatform
import site.techmoa.worker.scheduler.repository.OutboxRepository.Destination

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import site.techmoa.domain.event.ArticleNotification
import site.techmoa.domain.event.WebhookRateLimited
import site.techmoa.domain.event.WebhookSender
import site.techmoa.worker.scheduler.repository.OutboxRepository
import site.techmoa.worker.scheduler.repository.OutboxRepository.PendingDelivery

class OutboxDispatchServiceTest : BehaviorSpec({
    for (global in listOf(false, true)) {
        val scope = if (global) "전역" else "개별"
        Given("두 번째 글에서 $scope 제한이 발생하면") {
            Then("성공한 첫 글만 기록하고 제한 범위에 따라 다음 웹훅을 처리한다") {
                val mapper = jacksonObjectMapper()
                val repository = mockk<OutboxRepository>(relaxUnitFun = true)
                val sender = mockk<WebhookSender>(relaxUnitFun = true)
                every { sender.platform } returns WebhookPlatform.DISCORD
                val first = Destination(1, WebhookPlatform.DISCORD)
                val second = Destination(2, WebhookPlatform.DISCORD)
                val third = Destination(3, WebhookPlatform.SLACK)
                val slackSender = mockk<WebhookSender>(relaxUnitFun = true)
                every { slackSender.platform } returns WebhookPlatform.SLACK
                fun article(id: Long) = ArticleNotification(id, "title", "link", 0, "blog", "url")
                fun delivery(id: Long) = PendingDelivery(id, mapper.writeValueAsString(article(id)))
                val rateLimit = WebhookRateLimited(1500, global)
                every { repository.findReadyDestinations() } returns listOf(first, second, third)
                every { repository.findPendingDeliveries(first) } returns listOf(delivery(1), delivery(2), delivery(3))
                every { repository.findPendingDeliveries(second) } returnsMany listOf(listOf(delivery(4)), emptyList())
                every { repository.findPendingDeliveries(third) } returnsMany listOf(listOf(delivery(5)), emptyList())
                every { sender.publish(article(2)) } throws rateLimit

                OutboxDispatchService(repository, listOf(sender, slackSender), mapper).dispatchPending()

                verify(exactly = 1) { repository.recordDeliveryResults(first, listOf(1L), emptyMap(), rateLimit) }
                verify(exactly = 1) { slackSender.publish(article(5)) }
                verify(exactly = 0) { sender.publish(article(3)) }
                verify(exactly = if (global) 0 else 1) { sender.publish(article(4)) }
            }
        }
    }
})
