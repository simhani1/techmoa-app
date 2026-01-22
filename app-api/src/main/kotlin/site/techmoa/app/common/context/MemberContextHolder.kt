package site.techmoa.app.common.context

object MemberContextHolder {
    private val memberIdThreadLocal = ThreadLocal<Passport>()

    fun setPassport(passport: Passport) {
        memberIdThreadLocal.set(passport)
    }

    fun getPassport(): Passport {
        return memberIdThreadLocal.get()
    }

    fun clear() {
        memberIdThreadLocal.remove()
    }
}
