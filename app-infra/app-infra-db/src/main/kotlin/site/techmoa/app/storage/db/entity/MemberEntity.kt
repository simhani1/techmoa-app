package site.techmoa.app.storage.db.entity

import jakarta.persistence.*
import site.techmoa.app.common.auth.OauthProvider

@Entity
@Table(
    name = "member",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uk_provider_subject",
            columnNames = ["provider", "subject"]
        )
    ]
)
class MemberEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id", nullable = false)
    val id: Long = 0L,

    @Column(name = "email", nullable = false, length = 64)
    val email: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "provider", nullable = false, length = 20)
    val provider: OauthProvider,

    @Column(name = "subject", nullable = false, length = 64)
    val subject: String
) : BaseEntity()