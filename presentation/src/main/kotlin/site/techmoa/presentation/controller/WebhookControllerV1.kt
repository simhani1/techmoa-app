package site.techmoa.presentation.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import site.techmoa.application.service.WebhookService
import site.techmoa.presentation.common.template.ApiResponse
import site.techmoa.presentation.controller.request.SaveWebhookRequest
import site.techmoa.presentation.controller.response.SaveWebhookResponse

@RequestMapping("/v1/webhooks")
@RestController
class WebhookControllerV1(
    private val webhookService: WebhookService,
) {

    @PostMapping
    fun save(
        @RequestBody request: SaveWebhookRequest
    ): ResponseEntity<ApiResponse<SaveWebhookResponse>> {
        val webhook = webhookService.save(
            platform = request.toPlatform(),
            url = request.normalizedUrl(),
        )
        return ResponseEntity.ok(
            ApiResponse.success(
                SaveWebhookResponse(webhookId = webhook.id)
            )
        )
    }
}
