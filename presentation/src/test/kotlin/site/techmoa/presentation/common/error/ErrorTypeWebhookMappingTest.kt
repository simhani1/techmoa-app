package site.techmoa.presentation.common.error

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import org.springframework.http.HttpStatus
import site.techmoa.domain.exception.ErrorCode

class ErrorTypeWebhookMappingTest : BehaviorSpec({
    given("Webhook ErrorCode 매핑을 확인할 때") {
        `when`("DUPLICATED_WEBHOOK을 변환하면") {
            val errorType = ErrorType.from(ErrorCode.DUPLICATED_WEBHOOK)

            then("BAD_REQUEST와 매핑된다") {
                errorType shouldBe ErrorType.DUPLICATED_WEBHOOK
                errorType.status shouldBe HttpStatus.BAD_REQUEST
            }
        }

        `when`("INVALID_WEBHOOK_PLATFORM을 변환하면") {
            val errorType = ErrorType.from(ErrorCode.INVALID_WEBHOOK_PLATFORM)

            then("BAD_REQUEST와 매핑된다") {
                errorType shouldBe ErrorType.INVALID_WEBHOOK_PLATFORM
                errorType.status shouldBe HttpStatus.BAD_REQUEST
            }
        }

        `when`("INVALID_WEBHOOK_URL을 변환하면") {
            val errorType = ErrorType.from(ErrorCode.INVALID_WEBHOOK_URL)

            then("BAD_REQUEST와 매핑된다") {
                errorType shouldBe ErrorType.INVALID_WEBHOOK_URL
                errorType.status shouldBe HttpStatus.BAD_REQUEST
            }
        }
    }
})
