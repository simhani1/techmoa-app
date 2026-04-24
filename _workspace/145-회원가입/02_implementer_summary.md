# 구현 완료 요약: 자체 회원가입 기능

## 빌드 상태
- `./gradlew compileKotlin` : BUILD SUCCESSFUL

## 변경/생성 파일 목록

### domain (수정 3건)

| 파일 | 변경 유형 | 내용 |
|------|----------|------|
| `domain/src/main/kotlin/site/techmoa/domain/model/OauthProvider.kt` | 수정 | `LOCAL` 값 추가 |
| `domain/src/main/kotlin/site/techmoa/domain/exception/ErrorCode.kt` | 수정 | `DUPLICATED_LOGIN_ID` 추가 (MEMBER 섹션) |
| `domain/src/main/kotlin/site/techmoa/domain/exception/DomainException.kt` | 수정 | `DuplicatedLoginIdException` 클래스 추가 |

### infrastructure:mysql (생성 1건)

| 파일 | 변경 유형 | 내용 |
|------|----------|------|
| `infrastructure/mysql/src/main/resources/db/migration/V1.15__add_login_id_and_password_to_member.sql` | 생성 | member 테이블에 login_id(VARCHAR 50), password(VARCHAR 100) nullable 컬럼 추가 + uk_login_id 유니크 제약 |

### infrastructure:jpa (수정 3건)

| 파일 | 변경 유형 | 내용 |
|------|----------|------|
| `infrastructure/jpa/src/main/kotlin/site/techmoa/infrastructure/jpa/entity/MemberEntity.kt` | 수정 | `loginId`(nullable), `password`(nullable) 필드 추가 (기본값 null) |
| `infrastructure/jpa/src/main/kotlin/site/techmoa/infrastructure/jpa/repository/MemberRepository.kt` | 수정 | `existsByLoginId(loginId: String): Boolean` 메서드 추가 |
| `infrastructure/jpa/src/main/kotlin/site/techmoa/infrastructure/jpa/adapter/MemberAdapter.kt` | 수정 | `existsByLoginId()`, `saveLocal()` 구현 추가 |

### application (생성 3건, 수정 1건)

| 파일 | 변경 유형 | 내용 |
|------|----------|------|
| `application/src/main/kotlin/site/techmoa/application/port/PasswordPort.kt` | 생성 | `encode(rawPassword): String` 인터페이스 |
| `application/src/main/kotlin/site/techmoa/application/port/MemberPort.kt` | 수정 | `existsByLoginId()`, `saveLocal()` 메서드 추가 |
| `application/src/main/kotlin/site/techmoa/application/dto/SignupCommand.kt` | 생성 | `loginId`, `password` 필드를 가진 data class |
| `application/src/main/kotlin/site/techmoa/application/service/SignupService.kt` | 생성 | 중복 검증 -> 비밀번호 해싱 -> 회원 저장 -> 토큰 발급 흐름 |

### infrastructure:oauth (생성 1건, 수정 1건)

| 파일 | 변경 유형 | 내용 |
|------|----------|------|
| `infrastructure/oauth/src/main/kotlin/site/techmoa/infrastructure/oauth/adapter/PasswordAdapter.kt` | 생성 | BCryptPasswordEncoder로 PasswordPort 구현 |
| `infrastructure/oauth/build.gradle.kts` | 수정 | `spring-security-crypto` 의존성 추가 |

### presentation (생성 2건, 수정 1건)

| 파일 | 변경 유형 | 내용 |
|------|----------|------|
| `presentation/src/main/kotlin/site/techmoa/presentation/controller/request/SignupRequest.kt` | 생성 | `loginId`, `password` 필드를 가진 data class |
| `presentation/src/main/kotlin/site/techmoa/presentation/controller/SignupControllerV1.kt` | 생성 | `POST /v1/auth/signup`, 201 Created 응답 + 쿠키 설정 |
| `presentation/src/main/kotlin/site/techmoa/presentation/common/error/ErrorType.kt` | 수정 | `DUPLICATED_LOGIN_ID(CONFLICT, 409)` 추가 |

## 충돌 사항
- 없음

## 참고 사항
- MemberEntity에 loginId, password 필드를 nullable + 기본값 null로 추가하여 기존 OAuth 회원 생성 흐름에 영향 없음
- MemberPort에 메서드를 추가했으나 기존 메서드는 변경하지 않음
- spring-security-crypto 버전은 Spring BOM에 의해 자동 관리됨
