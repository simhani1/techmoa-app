package site.techmoa.application.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import site.techmoa.application.dto.AuthToken
import site.techmoa.application.dto.SignupCommand
import site.techmoa.application.port.AuthTokenPort
import site.techmoa.application.port.MemberPort
import site.techmoa.application.port.PasswordPort
import site.techmoa.domain.exception.DuplicatedLoginIdException

@Service
class SignupService(
    private val memberPort: MemberPort,
    private val passwordPort: PasswordPort,
    private val authTokenPort: AuthTokenPort,
) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    fun process(command: SignupCommand): AuthToken {
        log.info("[Signup] Processing signup for loginId: ${command.loginId.replace(Regex("[\r\n]"), "_")}")

        if (memberPort.existsByLoginId(command.loginId)) {
            throw DuplicatedLoginIdException("이미 사용 중인 아이디입니다. loginId: ${command.loginId}")
        }

        val encodedPassword = passwordPort.encode(command.password)
        val member = memberPort.saveLocal(command.loginId, encodedPassword)

        log.info("[Signup] Member created - memberId: ${member.id}")
        val authToken = authTokenPort.issue(member.id)

        log.info("[Signup] Token issued successfully")
        return authToken
    }
}
