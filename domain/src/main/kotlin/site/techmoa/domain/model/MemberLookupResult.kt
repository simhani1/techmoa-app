package site.techmoa.domain.model

sealed class MemberLookupResult {
    data class ExistingMember(val member: Member) : MemberLookupResult()
    data object NewMember : MemberLookupResult()
}