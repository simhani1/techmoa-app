package site.techmoa.app.storage.db.entity

import jakarta.persistence.*

@Entity
@Table(
    name = "member",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uk_oauth",
            columnNames = ["provider", "provider_user_id"]
        )
    ]
)
class MemberEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id", nullable = false)
    val id: Long = 0L,

    @Column(name = "email" ,nullable = false ,length = 64)
    val email: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "provider" ,nullable = false ,length = 20)
    val provider: OAuthProvider,

    @Column(name = "provider_user_id" ,nullable = false ,length = 64)
    val providerUserId: String
) : BaseEntity() {

    enum class OAuthProvider {
        KAKAO,
    }
}


