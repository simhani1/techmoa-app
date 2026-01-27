package site.techmoa.app.core.context

sealed class Passport() {
    data class Member(val memberId: Long) : Passport()
    object Guest : Passport()
}
