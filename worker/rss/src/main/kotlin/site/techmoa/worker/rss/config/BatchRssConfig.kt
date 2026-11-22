package site.techmoa.worker.rss.config

import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.task.SimpleAsyncTaskExecutor
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler

@Configuration
@EnableScheduling
class BatchRssConfig {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @Bean("rssTaskScheduler")
    fun rssTaskScheduler(): ThreadPoolTaskScheduler {
        val scheduler = ThreadPoolTaskScheduler()
        scheduler.threadNamePrefix = "rss-scheduler-"
        scheduler.setWaitForTasksToCompleteOnShutdown(true)
        scheduler.setAwaitTerminationSeconds(30)
        scheduler.setErrorHandler { ex ->
            log.error("[BatchRssConfig] Scheduled task execution failed", ex)
        }
        scheduler.initialize()
        return scheduler
    }

    @Bean("rssFetchExecutor", destroyMethod = "close")
    fun rssFetchExecutor(
        @Value("\${techmoa.rss.max-concurrent-fetches:4}") maxConcurrentFetches: Int,
    ): SimpleAsyncTaskExecutor {
        require(maxConcurrentFetches > 0) { "techmoa.rss.max-concurrent-fetches must be positive" }
        val executor = SimpleAsyncTaskExecutor("rss-fetch-")
        executor.setVirtualThreads(true)
        executor.concurrencyLimit = maxConcurrentFetches
        executor.setTaskTerminationTimeout(30_000)
        return executor
    }
}
