package site.techmoa.app.core.member.domain

sealed class MemberLookupResult {
    data class ExistingMember(val member: Member) : MemberLookupResult()
    data object NewMember : MemberLookupResult()
}