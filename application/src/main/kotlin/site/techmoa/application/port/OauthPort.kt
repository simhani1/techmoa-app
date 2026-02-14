package site.techmoa.application.port

import site.techmoa.application.dto.MemberResource

interface OauthPort {
    fun getMemberResource(code: String): MemberResource
}