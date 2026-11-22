# 구현 계획: 자체 회원가입 기능

## 1. 요구사항 요약

- id(로그인 식별자)와 password만으로 자체 회원가입을 처리한다.
- 비밀번호는 BCrypt로 해싱하여 저장한다.
- 중복 id 검증을 수행한다.
- 회원가입 성공 시 JWT 토큰을 발급하여 쿠키로 반환한다.

## 2. 설계 결정사항

### 2.1 기존 Member 도메인과의 관계

기존 `Member`는 OAuth 전용 구조(`provider`, `subject`, `email`)이다. 자체 회원가입은 `loginId`와 `password`를 사용하므로 기존 member 테이블을 확장한다.

- `OauthProvider` enum에 `LOCAL` 값을 추가한다.
- member 테이블에 `login_id`(VARCHAR 50, nullable)와 `password`(VARCHAR 100, nullable) 컬럼을 추가한다.
- LOCAL 회원: `provider=LOCAL`, `subject=loginId`, `login_id=loginId`, `password=해시값`, `email`은 빈 문자열.
- OAuth 회원: 기존과 동일, `login_id=NULL`, `password=NULL`.
- `login_id` 컬럼에 유니크 인덱스를 추가하여 중복 검증을 DB 레벨에서 보장한다.

### 2.2 비밀번호 해싱 전략

- Spring Security의 `BCryptPasswordEncoder`를 사용한다.
- application 레이어에 `PasswordPort` 인터페이스를 정의하고, infrastructure 레이어에서 BCrypt 구현체를 제공한다.
- domain 모듈은 순수 Kotlin을 유지하므로 해싱 로직을 넣지 않는다.

### 2.3 중복 ID 검증 전략

- `MemberPort`에 `existsByLoginId(loginId: String): Boolean` 메서드를 추가한다.
- 서비스 레이어에서 저장 전 중복 검증을 수행한다.
- DB 유니크 인덱스로 동시성 문제를 방어한다.

## 3. 영향받는 모듈 목록

| 모듈 | 변경 유형 |
|------|----------|
| domain | 모델 수정 (`OauthProvider`에 LOCAL 추가), 예외 추가 |
| application | 포트 추가/수정, 서비스 생성, DTO 추가 |
| presentation | 컨트롤러 추가, request/response 추가, ErrorType 추가 |
| infrastructure:jpa | 엔티티 수정, 리포지토리 수정, 어댑터 수정/추가 |
| infrastructure:mysql | Flyway 마이그레이션 추가 |
| infrastructure:oauth | PasswordPort 구현 어댑터 추가, build.gradle.kts 의존성 추가 |

## 4. API 스펙

### POST /v1/auth/signup

**Request Body:**
```json
{
  "loginId": "string (3~50자, 영문 소문자+숫자+언더스코어)",
  "password": "string (8~30자)"
}
```

**Response (201 Created):**
- Set-Cookie 헤더에 accessToken 포함
```json
{
  "resultType": "SUCCESS",
  "data": null,
  "errorMessage": null
}
```

**Error Responses:**
- 400 Bad Request: 입력값 검증 실패
- 409 Conflict: 중복된 loginId (`DUPLICATED_LOGIN_ID`)

## 5. DB 스키마 변경

### V1.15__add_login_id_and_password_to_member.sql

```sql
ALTER TABLE member
    ADD COLUMN login_id VARCHAR(50) NULL AFTER subject,
    ADD COLUMN password VARCHAR(100) NULL AFTER login_id;

ALTER TABLE member
    ADD CONSTRAINT uk_login_id UNIQUE (login_id);
```

## 6. 레이어별 구현 계획

### 6.1 domain 모듈

#### 수정: `domain/src/main/kotlin/site/techmoa/domain/model/OauthProvider.kt`
- `LOCAL` 값을 enum에 추가한다.

#### 수정: `domain/src/main/kotlin/site/techmoa/domain/exception/ErrorCode.kt`
- `DUPLICATED_LOGIN_ID` 추가.

#### 수정: `domain/src/main/kotlin/site/techmoa/domain/exception/DomainException.kt`
- `DuplicatedLoginIdException` 클래스 추가.

---

### 6.2 application 모듈

#### 생성: `application/src/main/kotlin/site/techmoa/application/port/PasswordPort.kt`
```kotlin
interface PasswordPort {
    fun encode(rawPassword: String): String
}
```
- 비밀번호 해싱을 추상화하는 포트.

#### 수정: `application/src/main/kotlin/site/techmoa/application/port/MemberPort.kt`
- `existsByLoginId(loginId: String): Boolean` 메서드 추가.
- `saveLocal(loginId: String, encodedPassword: String): Member` 메서드 추가.

#### 생성: `application/src/main/kotlin/site/techmoa/application/dto/SignupCommand.kt`
```kotlin
data class SignupCommand(
    val loginId: String,
    val password: String,
)
```
- 회원가입 요청 데이터를 전달하는 DTO.

#### 생성: `application/src/main/kotlin/site/techmoa/application/service/SignupService.kt`
```kotlin
@Service
class SignupService(
    private val memberPort: MemberPort,
    private val passwordPort: PasswordPort,
    private val authTokenPort: AuthTokenPort,
) {
    fun process(command: SignupCommand): AuthToken {
        // 1. 중복 loginId 검증
        // 2. 비밀번호 해싱
        // 3. 회원 저장 (provider=LOCAL, subject=loginId)
        // 4. JWT 토큰 발급
    }
}
```
- 자체 회원가입 유스케이스 오케스트레이션.

---

### 6.3 presentation 모듈

#### 생성: `presentation/src/main/kotlin/site/techmoa/presentation/controller/request/SignupRequest.kt`
```kotlin
data class SignupRequest(
    val loginId: String,
    val password: String,
)
```
- 요청 바인딩 및 검증.

#### 생성: `presentation/src/main/kotlin/site/techmoa/presentation/controller/SignupControllerV1.kt`
```kotlin
@RequestMapping("/v1/auth")
@RestController
class SignupControllerV1(
    private val signupService: SignupService,
    private val authCookieFactory: AuthCookieFactory,
) {
    @PostMapping("/signup")
    fun signup(@RequestBody request: SignupRequest): ResponseEntity<ApiResponse<Any>> {
        // 1. request -> SignupCommand 변환
        // 2. signupService.process() 호출
        // 3. 쿠키에 accessToken 설정, 201 반환
    }
}
```

#### 수정: `presentation/src/main/kotlin/site/techmoa/presentation/common/error/ErrorType.kt`
- `DUPLICATED_LOGIN_ID(HttpStatus.CONFLICT, ErrorCode.DUPLICATED_LOGIN_ID, "이미 사용 중인 아이디입니다.", LogLevel.INFO)` 추가.

---

### 6.4 infrastructure:jpa 모듈

#### 수정: `infrastructure/jpa/src/main/kotlin/site/techmoa/infrastructure/jpa/entity/MemberEntity.kt`
- `loginId` (nullable VARCHAR 50) 필드 추가.
- `password` (nullable VARCHAR 100) 필드 추가.

#### 수정: `infrastructure/jpa/src/main/kotlin/site/techmoa/infrastructure/jpa/repository/MemberRepository.kt`
- `existsByLoginId(loginId: String): Boolean` 메서드 추가.

#### 수정: `infrastructure/jpa/src/main/kotlin/site/techmoa/infrastructure/jpa/adapter/MemberAdapter.kt`
- `existsByLoginId()` 구현.
- `saveLocal()` 구현: `MemberEntity`를 LOCAL provider로 저장.

---

### 6.5 infrastructure:oauth 모듈

#### 생성: `infrastructure/oauth/src/main/kotlin/site/techmoa/infrastructure/oauth/adapter/PasswordAdapter.kt`
```kotlin
@Component
class PasswordAdapter : PasswordPort {
    private val encoder = BCryptPasswordEncoder()
    override fun encode(rawPassword: String): String = encoder.encode(rawPassword)
}
```

#### 수정: `infrastructure/oauth/build.gradle.kts`
- `org.springframework.security:spring-security-crypto` 의존성 추가 (BCryptPasswordEncoder 사용).

---

### 6.6 infrastructure:mysql 모듈

#### 생성: `infrastructure/mysql/src/main/resources/db/migration/V1.15__add_login_id_and_password_to_member.sql`
- 위 5번 참조.

## 7. 구현 순서

1. **domain**: `OauthProvider.LOCAL` 추가, `ErrorCode.DUPLICATED_LOGIN_ID` 추가, `DuplicatedLoginIdException` 추가
2. **infrastructure:mysql**: Flyway 마이그레이션 `V1.15` 추가
3. **infrastructure:jpa**: `MemberEntity` 필드 추가, `MemberRepository` 메서드 추가, `MemberAdapter` 수정
4. **application**: `PasswordPort` 생성, `MemberPort` 수정, `SignupCommand` 생성, `SignupService` 생성
5. **infrastructure:oauth**: `PasswordAdapter` 생성, build.gradle.kts 의존성 추가
6. **presentation**: `SignupRequest` 생성, `SignupControllerV1` 생성, `ErrorType` 수정

## 8. 기존 코드와의 통합 지점

- `Member` 도메인 모델 자체는 변경하지 않는다 (LOCAL 회원도 동일한 `Member` 모델로 표현).
- `OauthProvider`에 `LOCAL`을 추가하여 기존 OAuth 흐름과 호환을 유지한다.
- `AuthTokenPort`, `AuthCookieFactory`는 기존 것을 그대로 재사용한다.
- `MemberPort`에 메서드를 추가하되, 기존 `findByProviderAndSubject`, `save` 메서드는 변경하지 않는다.
- `GlobalExceptionHandler`는 `DomainException` 기반이므로 추가 설정 없이 새 예외를 처리한다.
