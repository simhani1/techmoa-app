package site.techmoa.app.core.member

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import site.techmoa.app.core.auth.dto.MemberResource
import site.techmoa.app.core.member.domain.Member
import site.techmoa.app.core.member.domain.MemberLookupResult
import site.techmoa.app.core.member.port.MemberPort

@Service
class MemberUseCase(
    private val memberPort: MemberPort
) {
    @Transactional
    fun findOrCreate(resource: MemberResource): Member {
        return when (val member = memberPort.findByProviderAndSubject(resource.provider, resource.subject)) {
            is MemberLookupResult.NewMember -> memberPort.save(resource)
            is MemberLookupResult.ExistingMember -> member.member
        }
    }
}
