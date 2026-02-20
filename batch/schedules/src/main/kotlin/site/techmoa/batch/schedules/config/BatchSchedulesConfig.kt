package site.techmoa.batch.schedules.config

import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler

@Configuration
@EnableScheduling
class BatchSchedulesConfig {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @Bean("schedulesTaskScheduler")
    fun schedulesTaskScheduler(): ThreadPoolTaskScheduler {
        val scheduler = ThreadPoolTaskScheduler()
        scheduler.poolSize = 4
        scheduler.threadNamePrefix = "schedules-"
        scheduler.setWaitForTasksToCompleteOnShutdown(true)
        scheduler.setAwaitTerminationSeconds(30)
        scheduler.setErrorHandler { ex ->
            log.error("[BatchSchedulesConfig] Scheduled task execution failed", ex)
        }
        scheduler.initialize()
        return scheduler
    }
}
