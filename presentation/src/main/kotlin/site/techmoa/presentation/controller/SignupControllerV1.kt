package site.techmoa.presentation.controller

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import site.techmoa.application.dto.SignupCommand
import site.techmoa.application.service.SignupService
import site.techmoa.presentation.common.cookie.AuthCookieFactory
import site.techmoa.presentation.common.template.ApiResponse
import site.techmoa.presentation.controller.request.SignupRequest

@RequestMapping("/v1/auth")
@RestController
class SignupControllerV1(
    private val signupService: SignupService,
    private val authCookieFactory: AuthCookieFactory,
) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @PostMapping("/signup")
    fun signup(@RequestBody request: SignupRequest): ResponseEntity<ApiResponse<Any>> {
        log.info("[Signup] Signup request received for loginId: ${request.loginId.replace(Regex("[\r\n]"), "_")}")
        val command = SignupCommand(
            loginId = request.loginId,
            password = request.password,
        )
        val token = signupService.process(command)

        log.info("[Signup] Signup successful, access token issued")
        val headers = authCookieFactory.accessTokenHeaders(token.accessToken)
        return ResponseEntity.status(HttpStatus.CREATED)
            .headers(headers)
            .body(ApiResponse.success())
    }
}
