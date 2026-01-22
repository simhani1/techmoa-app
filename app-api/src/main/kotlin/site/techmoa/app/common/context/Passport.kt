package site.techmoa.app.common.context

sealed class Passport() {
    data class Member(val memberId: Long) : Passport()
    object Guest : Passport()
}
