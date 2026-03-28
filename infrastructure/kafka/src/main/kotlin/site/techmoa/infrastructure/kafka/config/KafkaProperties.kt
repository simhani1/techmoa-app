package site.techmoa.infrastructure.kafka.config

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "spring.kafka")
data class KafkaProperties(
    val bootstrapServers: String,
    val producer: Producer,
    val consumer: Consumer,
) {
    fun buildProducerProperties(): Map<String, Any> {
        return buildMap {
            put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers)
            put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java)
            put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java)
            put(ProducerConfig.ACKS_CONFIG, producer.acks)
            putAll(producer.properties)
        }
    }

    fun buildConsumerProperties(): Map<String, Any> {
        return buildMap {
            put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers)
            put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java)
            put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java)
            put(ConsumerConfig.GROUP_ID_CONFIG, consumer.groupId)
            put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, consumer.autoOffsetReset)
            put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, consumer.enableAutoCommit)
            put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, consumer.maxPollRecords)
        }
    }

    data class Producer(
        val acks: String,
        val properties: Map<String, Any> = emptyMap(),
    )

    data class Consumer(
        val groupId: String,
        val autoOffsetReset: String,
        val enableAutoCommit: Boolean,
        val maxPollRecords: Int,
    )
}
