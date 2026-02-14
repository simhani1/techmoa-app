package site.techmoa.application.usecase

import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import site.techmoa.application.dto.MemberResource
import site.techmoa.application.port.MemberPort
import site.techmoa.domain.model.Member
import site.techmoa.domain.model.MemberLookupResult

@Component
class MemberUseCase(
    private val memberPort: MemberPort
) {

    @Transactional
    fun getOrSave(resource: MemberResource): Member {
        return when (val member = memberPort.findByProviderAndSubject(resource.provider, resource.subject)) {
            is MemberLookupResult.NewMember -> memberPort.save(resource)
            is MemberLookupResult.ExistingMember -> member.member
        }
    }
}