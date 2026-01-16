package site.techmoa.app.core.member

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import site.techmoa.app.core.auth.domain.ExternalUser

@Service
class MemberUseCase() {

    @Transactional
    fun findOrCreate(user: ExternalUser): Member {

        return Member(1, "sim")
    }
}