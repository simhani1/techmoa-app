package site.techmoa.worker.rss.config

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

class BatchRssConfigTest : BehaviorSpec({
    Given("RSS 수집 동시 요청 수를 설정하면") {
        Then("전용 실행기가 지정한 제한과 가상 스레드를 사용한다") {
            BatchRssConfig().rssFetchExecutor(2).use { executor ->
                executor.concurrencyLimit shouldBe 2
                CompletableFuture.supplyAsync({ Thread.currentThread().isVirtual }, executor)
                    .get(5, TimeUnit.SECONDS) shouldBe true
            }
        }
    }
})
