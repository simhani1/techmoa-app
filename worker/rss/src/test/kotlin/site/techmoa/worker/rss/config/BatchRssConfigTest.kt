package site.techmoa.worker.rss.config

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class BatchRssConfigTest : BehaviorSpec({
    Given("RSS 피드 5개의 응답이 모두 지연되면") {
        Then("앞선 피드를 기다리지 않고 다섯 요청 모두 가상 스레드에서 시작한다") {
            BatchRssConfig().rssFetchExecutor().use { executor ->
                val started = CountDownLatch(5)
                val release = CountDownLatch(1)
                val submission = CompletableFuture.supplyAsync {
                    List(5) {
                        CompletableFuture.supplyAsync({
                            started.countDown()
                            release.await()
                            Thread.currentThread().isVirtual
                        }, executor)
                    }
                }
                try {
                    started.await(5, TimeUnit.SECONDS) shouldBe true
                } finally {
                    release.countDown()
                }
                for (request in submission.get(5, TimeUnit.SECONDS)) {
                    request.get(5, TimeUnit.SECONDS) shouldBe true
                }
            }
        }
    }
})
