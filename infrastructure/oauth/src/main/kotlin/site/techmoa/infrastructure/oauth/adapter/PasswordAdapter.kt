package site.techmoa.infrastructure.oauth.adapter

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component
import site.techmoa.application.port.PasswordPort

@Component
class PasswordAdapter : PasswordPort {

    private val encoder = BCryptPasswordEncoder()

    override fun encode(rawPassword: String): String {
        return encoder.encode(rawPassword)
    }
}
