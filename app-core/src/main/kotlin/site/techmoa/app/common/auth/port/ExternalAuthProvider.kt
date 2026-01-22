package site.techmoa.app.common.auth.port

import site.techmoa.app.common.auth.dto.MemberResource

interface ExternalAuthProvider {
    fun getMemberResource(code: String): MemberResource
}