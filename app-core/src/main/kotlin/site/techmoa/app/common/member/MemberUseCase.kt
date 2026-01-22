package site.techmoa.app.common.member

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import site.techmoa.app.common.auth.dto.MemberResource
import site.techmoa.app.common.member.domain.Member
import site.techmoa.app.common.member.domain.MemberLookupResult
import site.techmoa.app.common.member.port.MemberPort

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
