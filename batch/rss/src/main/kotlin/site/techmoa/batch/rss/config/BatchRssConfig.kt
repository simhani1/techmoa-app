package site.techmoa.batch.rss.config

import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler

@Configuration
@EnableScheduling
class BatchRssConfig {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @Bean("rssTaskScheduler")
    fun rssTaskScheduler(): ThreadPoolTaskScheduler {
        val scheduler = ThreadPoolTaskScheduler()
        scheduler.poolSize = 4
        scheduler.threadNamePrefix = "rss-scheduler-"
        scheduler.setWaitForTasksToCompleteOnShutdown(true)
        scheduler.setAwaitTerminationSeconds(30)
        scheduler.setErrorHandler { ex ->
            log.error("[BatchRssConfig] Scheduled task execution failed", ex)
        }
        scheduler.initialize()
        return scheduler
    }
}