package site.techmoa.infrastructure.jpa.entity

import jakarta.persistence.*
import site.techmoa.domain.model.Webhook
import site.techmoa.domain.model.WebhookPlatform
import site.techmoa.domain.model.WebhookValidity

@Entity
@Table(name = "webhook")
class WebhookEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "webhook_id", nullable = false)
    val id: Long = 0L,

    @Column(name = "url", nullable = false, length = 1000)
    val url: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "validity", nullable = false, length = 20)
    val validity: WebhookValidity,

    @Enumerated(EnumType.STRING)
    @Column(name = "platform", nullable = false, length = 20)
    val platform: WebhookPlatform
) : BaseEntity() {

    companion object {
        fun from(webhook: Webhook): WebhookEntity {
            return WebhookEntity(
                id = webhook.id,
                url = webhook.url,
                validity = webhook.validity,
                platform = webhook.platform,
            )
        }
    }

    fun toDomain(): Webhook {
        return Webhook(
            id = id,
            url = url,
            validity = validity,
            platform = platform,
        )
    }
}
