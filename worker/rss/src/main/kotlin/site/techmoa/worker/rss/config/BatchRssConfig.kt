package site.techmoa.worker.rss.config

import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.core.task.SimpleAsyncTaskExecutor
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import java.net.http.HttpClient
import java.security.GeneralSecurityException
import java.time.Duration
import javax.net.ssl.SSLContext

@Configuration
@EnableScheduling
class BatchRssConfig {

    private val shutdownGracePeriod = Duration.ofSeconds(40)
    private val log = LoggerFactory.getLogger(this.javaClass)

    @Bean(destroyMethod = "close")
    fun rssHttpClient(): HttpClient {
        val builder = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(25))
            .followRedirects(HttpClient.Redirect.ALWAYS)
        try {
            val tls = SSLContext.getInstance("TLSv1.3")
            tls.init(null, null, null)
            builder.sslContext(tls)
        } catch (ex: GeneralSecurityException) {
            log.warn("RSS client using the default TLS context", ex)
        }
        return builder.build()
    }

    @Bean("rssTaskScheduler")
    fun rssTaskScheduler(): ThreadPoolTaskScheduler {
        val scheduler = ThreadPoolTaskScheduler()
        scheduler.threadNamePrefix = "rss-scheduler-"
        scheduler.setWaitForTasksToCompleteOnShutdown(true)
        scheduler.setAwaitTerminationMillis(shutdownGracePeriod.toMillis())
        scheduler.setErrorHandler { ex ->
            log.error("[BatchRssConfig] Scheduled task execution failed", ex)
        }
        scheduler.initialize()
        return scheduler
    }

    @Bean("rssFetchExecutor", destroyMethod = "close")
    fun rssFetchExecutor(): SimpleAsyncTaskExecutor {
        val executor = SimpleAsyncTaskExecutor("rss-fetch-")
        executor.setVirtualThreads(true)
        executor.setTaskTerminationTimeout(shutdownGracePeriod.toMillis())
        return executor
    }
}
