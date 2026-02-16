package site.techmoa.infrastructure.jpa.adapter

import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import site.techmoa.application.dto.MemberResource
import site.techmoa.application.port.MemberPort
import site.techmoa.domain.model.Member
import site.techmoa.domain.model.MemberLookupResult
import site.techmoa.domain.model.OauthProvider
import site.techmoa.infrastructure.jpa.entity.MemberEntity
import site.techmoa.infrastructure.jpa.repository.MemberRepository

@Repository
class MemberAdapter(
    private val memberRepository: MemberRepository
) : MemberPort {

    @Transactional(readOnly = true)
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

    @Transactional
    override fun save(resource: MemberResource): Member {
        return memberRepository.save(
            MemberEntity(
                email = resource.email,
                provider = resource.provider,
                subject = resource.subject,
            )
        ).let {
            Member(
                id = it.id,
                email = it.email,
                provider = it.provider,
                subject = it.subject,
            )
        }
    }
}