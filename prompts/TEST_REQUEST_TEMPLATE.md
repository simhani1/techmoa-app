# Test Request Template (Kotest + BehaviorSpec)

## 공통 규칙
- 테스트 프레임워크: `Kotest`
- 테스트 스타일: `BehaviorSpec` (항상 사용)
- 목 라이브러리: `MockK`
- 코루틴/비동기: `runTest` + `coEvery`/`coVerify`
- private/internal 직접 테스트: 금지
- 테스트명 포맷(항상 한국어 권장): `fun [상황에서는 결과가 어떻게 된다]()`
- 최소 Assertion 규칙:
  - 성공 케이스: 핵심 결과 + 핵심 상태값
  - 실패 케이스: 에러 타입 + `ErrorCode` + `ErrorType` + 메시지/필드

## 1) 빠른 입력 양식 (복붙용)
```text
REQUEST_TYPE: UNIT | INTEGRATION
TARGET_LAYER:
TARGET_CLASS_OR_METHOD:
SCOPE_EXCLUDE:
PRIORITY:
SPRING_TEST_ANNOTATION:
```

> `REQUEST_TYPE` 값에 따라 아래 2-1 또는 2-2 템플릿을 이어서 채워 주세요.

---

## 2) 목적별 템플릿

### 2-1. UNIT (순수 단위)

#### 2-1-1) 작업 범위
- 대상 모듈/레이어:
- 대상 클래스/메서드:
- 제외 범위:
- 목표/우선순위:
- 관련 이슈/요구사항:

#### 2-1-2) 테스트 기술 스택
- Spring 컨텍스트: 사용 안 함
- MockK 적용 범위: 인터페이스 단위 의존만 모킹
- 예외 생성/패턴:

#### 2-1-3) 테스트 작성 규칙
- Given/When/Then 구분: 사용
- 테스트명: `fun [조건] ... ()`
- Assertion 정책: 핵심 결과 + 에러 매핑(실패 시)
- private/internal 구현 테스트 금지 여부: 금지

#### 2-1-4) 레이어별 원칙
- Service/UseCase:
  - 비즈니스 규칙, 상태 전이, 권한/중복/멱등성 분기
  - 외부 의존은 MockK로 대체
- Repository:
  - 실제 DB 접근 금지
  - 인터페이스 호출/파라미터 전달 위주 검증

#### 2-1-5) 케이스 필수 항목
- 성공 케이스(최소 2개):
- 실패/예외 케이스(최소 2개):
- 경계값/변환 케이스:
- 필수 검증 항목:
  - 반환값/상태
  - `ErrorCode`, `ErrorType`, 메시지

#### 2-1-6) Fixture/데이터 규칙
- 위치: 테스트 클래스 내부 `private` fixture 함수 + `companion object`
- 생성 방식: 명시 값 하드코딩 + 빌더
- 공통 상수: 테스트 클래스 상단 상수화
- 공유 mutable 객체: 금지

#### 2-1-7) 완료 조건
- 실행: `./gradlew :<module>:test --tests "<package>.*"`
- 커버리지: 변경 라인 100%, 핵심 분기 95%+
- PR 체크: 중복 테스트 없음, flaky 테스트 없음

### 2-2. INTEGRATION (통합)

#### 2-2-1) 작업 범위
- 대상 모듈/레이어:
- 대상 클래스/메서드:
- 제외 범위:
- 목표/우선순위:
- 관련 이슈/요구사항:

#### 2-2-2) 테스트 기술 스택
- Spring 테스트 타입(1개만 선택):
  - `@WebMvcTest`
  - `@SpringBootTest`
  - `@DataJpaTest`
  - `@JdbcTest`
  - `@MongoTest`
- 외부 의존 처리:
  - Testcontainers / Stub / MockWebServer / WireMock

#### 2-2-3) 테스트 작성 규칙
- Given/When/Then 구분: 사용
- 테스트명: `fun [조건] ... ()`
- 실패 시 에러 매핑은 `ErrorCode`와 `ErrorType`까지 필수 검증
- private/internal 구현 테스트 금지 여부: 금지

#### 2-2-4) 레이어별 원칙
- Controller:
  - 요청/응답 스펙, 바인딩, 검증, 예외 매핑 중심
- Service/UseCase:
  - 트랜잭션/권한/동시성 경로
- Repository:
  - 매핑/쿼리/제약조건/롤백 경로

#### 2-2-5) 케이스 필수 항목
- 성공 케이스(최소 2개):
- 실패/예외 케이스(최소 3개):
  - 인증/권한
  - 바인딩/Validation
  - 비즈니스 에러 매핑
- 통합 전용 항목:
  - 연동 실패/재시도/타임아웃:
  - HTTP Status/Headers/Body:
  - 외부 의존 격리 전략:

#### 2-2-6) Fixture/데이터 규칙
- 위치: 테스트 클래스 내부 fixture + 테스트 모듈 fixture/SQL seed
- 생성 방식: 빌더 + 최소 데이터셋
- 공통 상수: 테스트 모듈 상수 사용 우선

#### 2-2-7) 완료 조건
- 실행: `./gradlew :<module>:test --tests "<package>.*"`
- 커버리지: 변경 분기 100%, 연동 경로 80%+
- PR 체크: 응답 스펙 일치, 환경 격리 정리, 네트워크 의존 명시

---

## 3) 샘플 (복붙 후 수정용)

### 예시 A - UNIT
- REQUEST_TYPE: `UNIT`
- TARGET_LAYER: `application`
- TARGET_CLASS_OR_METHOD: `OrderService.createOrder`
- SCOPE_EXCLUDE: 통합환경, Controller
- PRIORITY: 중복 주문 방지 + 수량 제약
- TARGET_TEST: `fun [중복 주문이면 DUPLICATE_ORDER를 반환한다]()`

### 예시 B - INTEGRATION
- REQUEST_TYPE: `INTEGRATION`
- TARGET_LAYER: `presentation`
- TARGET_CLASS_OR_METHOD: `WebhookControllerV1.save`
- SCOPE_EXCLUDE: E2E, 실서비스 외부 호출
- SPRING_TEST_ANNOTATION: `@WebMvcTest`
- PRIORITY: 요청 유효성 + 예외 매핑 검증
- TARGET_TEST: `fun [중복 웹훅이면 400과 DUPLICATED_WEBHOOK을 반환한다]()`
