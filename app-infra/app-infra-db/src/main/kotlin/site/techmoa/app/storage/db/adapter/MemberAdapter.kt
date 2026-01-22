package site.techmoa.app.storage.db.adapter

import org.springframework.stereotype.Repository
import site.techmoa.app.common.auth.OauthProvider
import site.techmoa.app.common.auth.dto.MemberResource
import site.techmoa.app.common.member.domain.Member
import site.techmoa.app.common.member.domain.MemberLookupResult
import site.techmoa.app.common.member.port.MemberPort
import site.techmoa.app.storage.db.entity.MemberEntity
import site.techmoa.app.storage.db.repository.MemberRepository

@Repository
class MemberAdapter(
    private val memberRepository: MemberRepository
) : MemberPort {
    override fun findByProviderAndSubject(provider: OauthProvider, subject: String): MemberLookupResult {
        return when (val entity = memberRepository.findByProviderAndSubjectOrNull(provider, subject)) {
            null -> {
                MemberLookupResult.NewMember
            }
            else -> {
                MemberLookupResult.ExistingMember(
                    Member(
                        id = entity.id,
                        email = entity.email,
                        provider = entity.provider,
                        subject = entity.subject,
                    )
                )
            }
        }
    }

    override fun save(resource: MemberResource): Member {
        val saved = memberRepository.save(
            MemberEntity(
                email = resource.email,
                provider = resource.provider,
                subject = resource.subject,
            )
        )

        return Member(
            id = saved.id,
            email = saved.email,
            provider = saved.provider,
            subject = saved.subject,
        )
    }
}
