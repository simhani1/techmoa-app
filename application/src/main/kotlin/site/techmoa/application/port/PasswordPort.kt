package site.techmoa.application.port

interface PasswordPort {
    fun encode(rawPassword: String): String
}
