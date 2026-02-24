package site.techmoa.batch.rss.config

import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import java.util.concurrent.Executor

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

    @Bean("rssFetchExecutor")
    fun rssFetchExecutor(): Executor {
        val executor = ThreadPoolTaskExecutor()
        executor.corePoolSize = 4
        executor.maxPoolSize = 4
        executor.setQueueCapacity(100)
        executor.setThreadNamePrefix("rss-fetch-")
        executor.setWaitForTasksToCompleteOnShutdown(true)
        executor.initialize()
        return executor
    }
}