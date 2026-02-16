package site.techmoa.infrastructure.jpa.entity

import jakarta.persistence.*
import site.techmoa.domain.model.Member
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

    @JoinColumn(name = "member_id", nullable = false)
    val memberId: Long,

    @Enumerated(EnumType.STRING)
    @Column(name = "validity", nullable = false, length = 20)
    val validity: WebhookValidity = WebhookValidity.VALID,

    @Enumerated(EnumType.STRING)
    @Column(name = "platform", nullable = false, length = 20)
    val platform: WebhookPlatform = WebhookPlatform.DISCORD,
) : BaseEntity() {
    companion object {
        fun from(webhook: Webhook): WebhookEntity {
            return WebhookEntity(
                id = webhook.id,
                url = webhook.url,
                memberId = webhook.owner.id,
                validity = webhook.validity,
                platform = webhook.platform,
            )
        }
    }

    fun toDomain(owner: Member): Webhook {
        require(owner.id == memberId) { "owner.id and memberId must be same." }

        return Webhook(
            id = id,
            url = url,
            owner = owner,
            validity = validity,
            platform = platform,
        )
    }
}
