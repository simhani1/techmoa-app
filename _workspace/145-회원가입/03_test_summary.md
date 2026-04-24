# 테스트 완료 요약: 자체 회원가입 기능

## 실행 결과
- `./gradlew :application:test --tests "site.techmoa.application.service.SignupServiceTest"` : **4/4 PASSED**
- `./gradlew :presentation:test --tests "site.techmoa.presentation.controller.SignupControllerV1Test"` : **5/5 PASSED**

## 테스트 목록

### 1. SignupServiceTest (application 레이어 - 단위 테스트)

| # | 테스트 케이스 | 유형 | 검증 항목 |
|---|-------------|------|----------|
| 1 | 유효한 loginId와 password로 가입하면 토큰을 발급하여 반환한다 | 성공 | accessToken 값, 포트 호출 순서 |
| 2 | 다른 유효한 loginId로 가입하면 해당 회원의 토큰을 발급하여 반환한다 | 성공 | 다른 회원의 accessToken, memberId별 토큰 발급 |
| 3 | 이미 존재하는 loginId로 가입하면 DuplicatedLoginIdException이 발생하고 저장은 수행되지 않는다 | 실패 | ErrorCode.DUPLICATED_LOGIN_ID, 메시지, 후속 포트 미호출 |
| 4 | 비밀번호 인코딩 후 회원 저장이 실패하면 예외가 전파되고 토큰 발급은 수행되지 않는다 | 실패 | RuntimeException 전파, 토큰 발급 미호출 |

### 2. SignupControllerV1Test (presentation 레이어 - 통합 테스트)

| # | 테스트 케이스 | 유형 | 검증 항목 |
|---|-------------|------|----------|
| 1 | 유효한 loginId와 password로 요청하면 201과 accessToken 쿠키를 반환한다 | 성공 | HTTP 201, resultType=SUCCESS, Set-Cookie 헤더 |
| 2 | 다른 유효한 loginId로 요청하면 201과 해당 회원의 accessToken 쿠키를 반환한다 | 성공 | HTTP 201, 다른 토큰의 쿠키 |
| 3 | 중복된 loginId로 요청하면 409와 DUPLICATED_LOGIN_ID를 반환한다 | 실패 | HTTP 409, ErrorCode, ErrorType 메시지 |
| 4 | 요청 본문이 비어 있으면 400 에러를 반환한다 | 실패 | HTTP 400, 서비스 미호출 |
| 5 | Content-Type 없이 요청하면 415 에러를 반환한다 | 실패 | HTTP 415, 서비스 미호출 |

## 생성 파일

| 파일 | 테스트 수 |
|------|----------|
| `application/src/test/kotlin/site/techmoa/application/service/SignupServiceTest.kt` | 4 |
| `presentation/src/test/kotlin/site/techmoa/presentation/controller/SignupControllerV1Test.kt` | 5 |

## 스킵 항목

### MemberAdapter (infrastructure:jpa)
- `existsByLoginId()`, `saveLocal()` 메서드는 JPA 리포지토리 위임 + 엔티티 매핑으로 구성되어 있어, 실제 DB가 필요한 통합 테스트가 적절하다.
- 현재 프로젝트에 `@DataJpaTest` 기반 테스트 인프라(Testcontainers 등)가 구성되어 있지 않으므로 스킵하였다.
- 향후 Testcontainers 기반 통합 테스트 인프라 도입 시 추가 권장.

## 커버리지 요약
- **SignupService.process()**: 모든 분기(중복 검증 성공/실패, 저장 성공/실패) 커버
- **SignupControllerV1.signup()**: 정상 응답, 비즈니스 예외 매핑, 바인딩 에러 커버
- **ErrorType.DUPLICATED_LOGIN_ID**: HTTP 409 + ErrorCode 매핑 검증 완료
