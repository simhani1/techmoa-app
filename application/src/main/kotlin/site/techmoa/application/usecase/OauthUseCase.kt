package site.techmoa.application.usecase

import org.springframework.stereotype.Component
import site.techmoa.application.dto.MemberResource
import site.techmoa.application.port.OauthPort

@Component
class OauthUseCase(
    private val oauthPort: OauthPort
) {

    fun fetchMemberResource(code: String): MemberResource {
        return oauthPort.getMemberResource(code)
    }
}