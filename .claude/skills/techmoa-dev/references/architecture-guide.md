# TechMoa 아키텍처 가이드

에이전트가 기존 코드베이스의 패턴을 빠르게 파악할 때 참조하는 레퍼런스.

## 목차

1. [모듈 의존성 방향](#모듈-의존성-방향)
2. [레이어별 핵심 패턴](#레이어별-핵심-패턴)
3. [이벤트/아웃박스 패턴](#이벤트아웃박스-패턴)
4. [인증 흐름](#인증-흐름)

---

## 모듈 의존성 방향

```
presentation → application → domain ← infrastructure
                                    ← worker
```

- domain은 어떤 모듈에도 의존하지 않는다 (순수 Kotlin)
- application은 domain에만 의존한다
- presentation은 application과 domain에 의존한다
- infrastructure는 application과 domain에 의존한다 (Port 구현)
- worker는 독자적 도메인 모델을 가지되, 공통 domain 모듈의 이벤트를 참조할 수 있다
- boot는 모든 모듈을 조합하는 진입점이다

## 레이어별 핵심 패턴

### Domain 모델 패턴

```kotlin
// domain/model/Webhook.kt 스타일
data class Webhook(
    val id: Long,
    val url: String,
    val platform: WebhookPlatform,
    val validity: WebhookValidity,
) {
    // 도메인 로직은 모델 내부 메서드로
}
```

### Application Port/UseCase 패턴

```kotlin
// application/port/WebhookPort.kt
interface WebhookPort {
    fun save(webhook: Webhook): Webhook
    fun findByUrl(url: String): Webhook?
}

// application/usecase/SaveWebhookUseCase.kt
@Service
class SaveWebhookUseCase(
    private val webhookPort: WebhookPort,
) {
    fun execute(command: SaveWebhookCommand): Webhook { ... }
}
```

### Presentation Controller 패턴

```kotlin
// presentation/controller/WebhookControllerV1.kt
@RestController
@RequestMapping("/api/v1/webhooks")
class WebhookControllerV1(
    private val saveWebhookUseCase: SaveWebhookUseCase,
) {
    @PostMapping
    fun save(@RequestBody request: SaveWebhookRequest): ApiResponse<SaveWebhookResponse> { ... }
}
```

### Infrastructure Adapter 패턴

```kotlin
// infrastructure/jpa/adapter/WebhookAdapter.kt
@Component
class WebhookAdapter(
    private val webhookJpaRepository: WebhookJpaRepository,
) : WebhookPort {
    override fun save(webhook: Webhook): Webhook { ... }
    override fun findByUrl(url: String): Webhook? { ... }
}
```

### Flyway 마이그레이션 패턴

- 경로: `infrastructure/mysql/src/main/resources/db/migration/`
- 네이밍: `V1.{N}__{snake_case_description}.sql`
- 현재 마지막 버전: `V1.14`

## 이벤트/아웃박스 패턴

TechMoa는 트랜잭셔널 아웃박스 패턴을 사용하여 이벤트를 발행한다:

1. 도메인 이벤트 발생 → `outbox_messages` 테이블에 PENDING 상태로 저장
2. 스케줄러(`worker/scheduler`)가 PENDING 레코드를 스캔
3. Kafka로 메시지를 퍼블리시 (`infrastructure/kafka`)
4. Consumer가 메시지를 수신하여 웹훅 발송 (`infrastructure/rest`)

```
[도메인 이벤트] → [outbox_messages (PENDING)]
                        ↓ (스케줄러 스캔)
                  [Kafka Publisher]
                        ↓
                  [Kafka Consumer]
                        ↓
                  [Discord Webhook 발송]
```

## 인증 흐름

- Kakao OAuth → OIDC 검증 → JWT 토큰 발급
- `@AuthRequired`: 인증 필수 엔드포인트
- `@AuthOptional`: 인증 선택 엔드포인트
- `MemberContextHolder`: 요청 스코프 내 인증 정보 보관
