# 리뷰 리포트: 자체 회원가입 기능

## 최종 판정: PASS (조건부)

CRITICAL 이슈 0건, MAJOR 이슈 1건, MINOR 이슈 3건.
MAJOR 이슈는 보안 관련이므로 배포 전 수정을 권장한다.

---

## 1. 아키텍처 준수 검증

| 항목 | 결과 | 비고 |
|------|------|------|
| domain 모듈에 Spring 의존성 없음 | PASS | `domain/build.gradle.kts`에 의존성 없음, import 확인 완료 |
| application -> domain 방향 의존성만 존재 | PASS | SignupService는 domain 예외만 참조 |
| Port가 application에 정의, Adapter가 infrastructure에서 구현 | PASS | PasswordPort(application) -> PasswordAdapter(oauth), MemberPort(application) -> MemberAdapter(jpa) |
| presentation은 application의 Service만 호출 | PASS | SignupControllerV1은 SignupService만 호출 |

## 2. 레이어 간 정합성 검증

| 항목 | 결과 | 비고 |
|------|------|------|
| Controller Request DTO <-> Service Command 필드 일치 | PASS | SignupRequest(loginId, password) -> SignupCommand(loginId, password) |
| JPA Entity <-> Domain Model 매핑 | PASS | MemberEntity -> Member 변환이 MemberAdapter.saveLocal()에서 올바르게 수행됨 |
| Flyway DDL <-> JPA Entity 컬럼 매핑 | PASS | V1.15: login_id VARCHAR(50) NULL, password VARCHAR(100) NULL == MemberEntity 필드 |
| ErrorCode <-> ErrorType <-> HTTP Status 매핑 | PASS | DUPLICATED_LOGIN_ID -> HttpStatus.CONFLICT(409) |
| Port 인터페이스 <-> Adapter 구현 일치 | PASS | MemberPort.existsByLoginId(), saveLocal() 모두 MemberAdapter에서 구현됨 |
| MemberEntity uniqueConstraint vs DDL | PASS | uk_login_id는 DDL에서만 정의, JPA @Table에는 uk_provider_subject만 있음. JPA 유니크 제약은 DDL 생성용이므로 Flyway 사용 시 DDL에만 있어도 정상 |

## 3. 기존 기능 호환성 검증

| 항목 | 결과 | 비고 |
|------|------|------|
| 기존 MemberPort 메서드 변경 없음 | PASS | findByProviderAndSubject, save 메서드 시그니처 유지 |
| 기존 MemberAdapter.save() 영향 없음 | PASS | 기존 save()는 loginId, password를 전달하지 않으므로 null 기본값 사용 |
| MemberEntity 생성자 기본값 | PASS | loginId=null, password=null로 기본값 설정되어 기존 코드 호환 |
| OauthProvider.LOCAL 추가 | PASS | 기존 KAKAO 값에 영향 없음 |
| login_id 컬럼 nullable + unique | PASS | 기존 OAuth 회원은 login_id=NULL이므로 유니크 제약에 걸리지 않음 (MySQL에서 NULL은 UNIQUE 비교에서 제외) |

## 4. 보안 검증

| 항목 | 결과 | 비고 |
|------|------|------|
| 비밀번호 BCrypt 해싱 | PASS | PasswordAdapter에서 BCryptPasswordEncoder 사용 |
| 해싱된 비밀번호만 DB 저장 | PASS | SignupService에서 encode() 후 saveLocal()에 encodedPassword 전달 |
| 입력값 검증 (loginId) | **MAJOR** | SignupRequest에 validation annotation 없음 -- 아래 이슈 #1 참조 |
| 로그에 비밀번호 노출 없음 | PASS | 로그에 loginId만 기록, password는 기록하지 않음 |

## 5. DB 마이그레이션 검증

| 항목 | 결과 | 비고 |
|------|------|------|
| 마이그레이션 버전 번호 순서 | PASS | V1.14 다음 V1.15, 충돌 없음 |
| DDL 안전성 | PASS | ALTER TABLE ADD COLUMN은 nullable이므로 기존 데이터에 영향 없음 |
| 유니크 인덱스 | PASS | login_id에 uk_login_id 유니크 제약 추가 |
| password 컬럼 길이 | PASS | VARCHAR(100)은 BCrypt 해시($2a$10$..., 60자)를 충분히 수용 |

## 6. 테스트 품질 검증

| 항목 | 결과 | 비고 |
|------|------|------|
| 성공 케이스 >= 2개 | PASS | SignupServiceTest 2개, SignupControllerV1Test 2개 |
| 실패 케이스 >= 2개 | PASS | SignupServiceTest 2개, SignupControllerV1Test 3개 |
| Assertion이 핵심 결과 검증 | PASS | accessToken 값, ErrorCode, HTTP Status 모두 검증 |
| BehaviorSpec 스타일 일관성 | PASS | given/when/then 구조 사용 |
| Fixture가 companion object에 정의 | PASS | 상수는 companion object, 팩토리 함수는 파일 레벨에 정의 |
| ErrorCode + ErrorType 교차 검증 | PASS | Controller 테스트에서 409 + DUPLICATED_LOGIN_ID + 메시지 검증 |

## 7. 코드 품질 검증

| 항목 | 결과 | 비고 |
|------|------|------|
| 기존 컨벤션 준수 | PASS | AuthControllerV1, LoginService 패턴과 동일한 구조 |
| 트랜잭션 관리 패턴 | PASS | Adapter 레벨에서 @Transactional 사용 (LoginService와 동일 패턴) |
| 로깅 패턴 | PASS | 기존 AuthControllerV1, LoginService와 동일한 로깅 스타일 |
| 불필요한 코드 변경 없음 | PASS | 기존 파일은 필요한 부분만 수정 |

---

## 발견된 이슈 목록

### 이슈 #1 [MAJOR] -- 입력값 검증 누락

**파일:** `presentation/src/main/kotlin/site/techmoa/presentation/controller/request/SignupRequest.kt:3-6`

**설명:** 설계 문서(01_architect_plan.md)에서 loginId는 "3~50자, 영문 소문자+숫자+언더스코어", password는 "8~30자"로 명시했으나, SignupRequest에 어떤 검증 로직도 없다. 빈 문자열, 1000자 문자열, SQL injection 패턴 등이 그대로 서비스 레이어로 전달된다.

**심각도:** MAJOR -- 빈 loginId("")로 가입 시 subject=""인 회원이 생성되고, 이후 동일한 빈 loginId로 재가입 시 유니크 제약 위반이 발생하는 등 예기치 않은 동작 가능.

**수정 제안:** 두 가지 방법 중 하나 선택:
1. (Jakarta Validation) SignupRequest에 `@field:NotBlank`, `@field:Size`, `@field:Pattern` 추가 + 컨트롤러에 `@Valid` 추가
2. (기존 프로젝트 패턴) SaveWebhookRequest처럼 SignupRequest 내부에 검증 메서드를 추가하고, 검증 실패 시 DomainException 하위 예외를 throw

기존 프로젝트가 SaveWebhookRequest에서 수동 검증 패턴을 사용하므로 방법 2가 컨벤션에 부합한다.

---

### 이슈 #2 [MINOR] -- SignupService에 @Transactional 부재로 인한 잠재적 정합성 문제

**파일:** `application/src/main/kotlin/site/techmoa/application/service/SignupService.kt:21`

**설명:** `existsByLoginId()`와 `saveLocal()`이 각각 별도의 트랜잭션(@Transactional이 MemberAdapter에 있음)으로 실행된다. 두 호출 사이에 동일 loginId로 다른 요청이 들어오면 중복 검증을 통과한 후 DB 유니크 제약 위반이 발생한다.

**심각도:** MINOR -- DB 유니크 인덱스(uk_login_id)가 최종 방어선 역할을 하므로 데이터 손상은 발생하지 않는다. 다만 DataIntegrityViolationException이 GlobalExceptionHandler에서 500으로 처리될 수 있다.

**수정 제안:** 현재 LoginService도 동일 패턴이므로 컨벤션상 허용 가능하나, 향후 DB 유니크 위반 예외를 409로 변환하는 처리를 추가하면 더 견고해진다.

---

### 이슈 #3 [MINOR] -- MemberEntity에 uk_login_id 유니크 제약이 JPA 레벨에 누락

**파일:** `infrastructure/jpa/src/main/kotlin/site/techmoa/infrastructure/jpa/entity/MemberEntity.kt:6-13`

**설명:** DDL(V1.15)에서 `uk_login_id UNIQUE (login_id)`를 추가했으나, MemberEntity의 `@Table` 어노테이션에는 해당 유니크 제약이 없다. Flyway를 사용하므로 런타임에 문제는 없으나, JPA 스키마 검증(`validate` 모드)이나 코드 가독성 측면에서 불일치가 있다.

**심각도:** MINOR -- Flyway가 DDL을 관리하므로 실질적 영향 없음.

**수정 제안:** `@Table`의 `uniqueConstraints`에 `UniqueConstraint(name = "uk_login_id", columnNames = ["login_id"])` 추가.

---

### 이슈 #4 [MINOR] -- SignupControllerV1을 별도 컨트롤러로 분리한 설계 결정

**파일:** `presentation/src/main/kotlin/site/techmoa/presentation/controller/SignupControllerV1.kt`

**설명:** 기존 인증 관련 컨트롤러로 AuthControllerV1(`/v1/oauth`)이 있다. SignupControllerV1은 `/v1/auth`를 사용하며 별도 클래스로 분리되었다. 기능적 문제는 없으나, 향후 자체 로그인(POST /v1/auth/login) 등이 추가되면 이 컨트롤러에 모이게 될 것이므로 현재 분리는 합리적이다.

**심각도:** MINOR -- 설계 결정에 대한 참고 사항.

**수정 제안:** 없음. 현재 구조가 적절하다.

---

## 권장 사항

1. **[우선] 이슈 #1 수정** -- 입력값 검증을 추가하라. 기존 SaveWebhookRequest 패턴에 따라 SignupRequest 내부에 검증 로직을 추가하는 것을 권장한다.
2. **[선택] 이슈 #2** -- DB 유니크 제약 위반(DataIntegrityViolationException)을 409 응답으로 변환하는 예외 처리를 GlobalExceptionHandler에 추가하면 동시성 상황에서 사용자 경험이 개선된다.
3. **[선택] 이슈 #3** -- MemberEntity에 JPA 유니크 제약을 추가하여 코드와 DDL의 일관성을 유지하라.
