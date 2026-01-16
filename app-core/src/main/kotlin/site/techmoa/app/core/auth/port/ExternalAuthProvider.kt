package site.techmoa.app.core.auth.port

import site.techmoa.app.core.auth.dto.MemberResource

interface ExternalAuthProvider {
    fun getMemberResource(code: String): MemberResource
}