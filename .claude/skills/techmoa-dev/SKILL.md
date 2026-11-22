---
name: techmoa-dev
description: "TechMoa 백엔드 기능 개발 파이프라인 오케스트레이터. 새 기능 구현, API 추가, 도메인 모델 변경, 버그 수정, 리팩토링 등 모든 백엔드 개발 작업을 조율한다. 헥사고날 아키텍처 기반 멀티모듈(domain/application/presentation/infrastructure/worker) 구현을 architect → implementer → test-writer → reviewer 파이프라인으로 수행. 후속 작업: 결과 수정, 부분 재실행, 업데이트, 보완, 다시 실행, 이전 결과 개선, 리뷰 반영, 테스트 보완 요청 시에도 반드시 이 스킬을 사용."
---

# TechMoa Dev Orchestrator

TechMoa 백엔드 개발을 architect → implementer → test-writer → reviewer 파이프라인으로 조율하는 통합 스킬.

## 실행 모드: 서브 에이전트

헥사고날 아키텍처의 레이어별 순차 의존성이 강하므로, 파이프라인 패턴 + 서브 에이전트 모드를 사용한다. 각 에이전트의 산출물이 다음 에이전트의 입력이 된다.

## 에이전트 구성

| 에이전트 | subagent_type | 역할 | 출력 |
|---------|--------------|------|------|
| architect | architect | 요구사항 분석, 설계, 구현 계획 | `_workspace/01_architect_plan.md` |
| implementer | implementer | 전 레이어 코드 구현 | 코드 파일 + `_workspace/02_implementer_summary.md` |
| test-writer | test-writer | Kotest/BehaviorSpec 테스트 | 테스트 파일 + `_workspace/03_test_summary.md` |
| reviewer | reviewer | 아키텍처 준수, 정합성 검증 | `_workspace/04_review_report.md` |

## 워크플로우

### Phase 0: 컨텍스트 확인

기존 산출물 존재 여부를 확인하여 실행 모드를 결정한다:

1. `_workspace/` 디렉토리 존재 여부 확인
2. 실행 모드 결정:
   - **`_workspace/` 미존재** → 초기 실행. Phase 1로 진행
   - **`_workspace/` 존재 + 사용자가 부분 수정 요청** → 부분 재실행. 해당 에이전트만 재호출
   - **`_workspace/` 존재 + 새 입력 제공** → 새 실행. 기존 `_workspace/`를 `_workspace_{YYYYMMDD_HHMMSS}/`로 이동 후 Phase 1 진행

### Phase 1: 준비

1. 사용자 입력에서 작업 유형을 파악한다:
   - **기능 개발**: 새 API, 새 도메인 모델, 새 워커 등
   - **버그 수정**: 기존 코드의 오류 수정
   - **리팩토링**: 구조 변경, 모듈 이동 등
2. `_workspace/` 디렉토리를 생성한다
3. 사용자 요구사항을 `_workspace/00_input.md`에 저장한다

### Phase 2: 설계 (architect)

architect 에이전트를 호출하여 구현 계획을 수립한다:

```
Agent(
  description: "TechMoa 아키텍처 설계",
  subagent_type: "architect",
  model: "opus",
  prompt: "다음 요구사항에 대한 구현 계획을 수립하라.
    요구사항: {사용자 입력 요약}
    _workspace/00_input.md를 읽고, 기존 코드베이스를 탐색하여
    _workspace/01_architect_plan.md에 구현 계획을 작성하라."
)
```

산출물 확인: `_workspace/01_architect_plan.md` 존재 및 내용 검토

### Phase 3: 구현 (implementer)

implementer 에이전트를 호출하여 코드를 구현한다:

```
Agent(
  description: "TechMoa 멀티모듈 구현",
  subagent_type: "implementer",
  model: "opus",
  prompt: "architect의 계획에 따라 코드를 구현하라.
    _workspace/01_architect_plan.md를 읽고, 레이어별 순서대로
    (domain → application → presentation → infrastructure) 구현하라.
    완료 후 _workspace/02_implementer_summary.md에 변경 파일 목록을 작성하라."
)
```

산출물 확인: 코드 파일 생성/수정 + `_workspace/02_implementer_summary.md`

### Phase 4: 테스트 (test-writer)

test-writer 에이전트를 호출하여 테스트를 작성한다:

```
Agent(
  description: "TechMoa 테스트 작성",
  subagent_type: "test-writer",
  model: "opus",
  prompt: "구현된 코드에 대한 테스트를 작성하라.
    먼저 prompts/TEST_REQUEST_TEMPLATE.md를 읽고 규칙을 파악하라.
    _workspace/01_architect_plan.md와 _workspace/02_implementer_summary.md를 읽고,
    구현된 코드를 분석하여 테스트를 작성하라.
    완료 후 _workspace/03_test_summary.md에 테스트 목록을 작성하라."
)
```

산출물 확인: 테스트 파일 생성 + `_workspace/03_test_summary.md`

### Phase 5: 검증 (reviewer)

reviewer 에이전트를 호출하여 전체를 검증한다:

```
Agent(
  description: "TechMoa 코드 리뷰",
  subagent_type: "reviewer",
  model: "opus",
  prompt: "_workspace/ 아래의 모든 산출물과 실제 코드를 읽고 리뷰하라.
    아키텍처 준수, 레이어 간 정합성, 테스트 품질을 검증하라.
    결과를 _workspace/04_review_report.md에 작성하라."
)
```

산출물 확인: `_workspace/04_review_report.md`의 PASS/FAIL 판정

### Phase 6: 결과 보고 및 수정 루프

1. `_workspace/04_review_report.md`를 읽는다
2. **PASS인 경우:** 사용자에게 결과 요약을 보고하고 종료
3. **FAIL인 경우 (CRITICAL 이슈 존재):**
   - CRITICAL 이슈 목록을 사용자에게 보고한다
   - 사용자 승인 후 implementer를 재호출하여 수정한다 (최대 1회)
   - 수정 후 reviewer를 재호출하여 재검증한다
4. `_workspace/` 디렉토리를 보존한다 (삭제하지 않음)

## 데이터 흐름

```
[사용자 입력]
    ↓
[Phase 1] → _workspace/00_input.md
    ↓
[architect] → _workspace/01_architect_plan.md
    ↓
[implementer] → 코드 파일 + _workspace/02_implementer_summary.md
    ↓
[test-writer] → 테스트 파일 + _workspace/03_test_summary.md
    ↓
[reviewer] → _workspace/04_review_report.md
    ↓
[결과 보고] → 사용자에게 요약
```

## 부분 재실행 가이드

사용자가 특정 부분만 수정을 요청할 때:

| 요청 유형 | 재실행 에이전트 | 입력 |
|----------|---------------|------|
| "설계 수정" | architect | 기존 plan + 피드백 |
| "코드 수정" | implementer | 기존 plan + 피드백 |
| "테스트 보완" | test-writer | 기존 summary + 피드백 |
| "리뷰만 다시" | reviewer | 기존 산출물 전체 |

부분 재실행 시 해당 에이전트의 프롬프트에 "이전 산출물이 `_workspace/`에 있으니 읽고 피드백을 반영하라"는 지시를 포함한다.

## 에러 핸들링

| 상황 | 전략 |
|------|------|
| 에이전트 1개 실패 | 1회 재시도. 재실패 시 사용자에게 알리고 진행 여부 확인 |
| architect 실패 | 파이프라인 중단. 사용자에게 요구사항 명확화 요청 |
| implementer 실패 | 부분 구현 결과 보존. 실패 지점부터 재시도 가능 |
| test-writer 실패 | 구현 코드는 유지. 테스트만 재시도 |
| reviewer 실패 | 구현 + 테스트는 유지. 수동 리뷰 권고 |

## 테스트 시나리오

### 정상 흐름
1. 사용자가 "블로그 구독 취소 API를 구현해줘"라고 요청
2. Phase 1에서 기능 개발로 분류, `_workspace/00_input.md` 생성
3. Phase 2에서 architect가 domain 모델 변경 + API 스펙 + 마이그레이션 계획 수립
4. Phase 3에서 implementer가 domain → application → presentation → infrastructure 순서로 구현
5. Phase 4에서 test-writer가 UseCase 단위 테스트 + Controller 통합 테스트 작성
6. Phase 5에서 reviewer가 전체 PASS 판정
7. Phase 6에서 사용자에게 결과 요약 보고

### 에러 흐름
1. Phase 5에서 reviewer가 CRITICAL 이슈 발견 (JPA Entity 컬럼명과 마이그레이션 불일치)
2. Phase 6에서 사용자에게 이슈 보고
3. 사용자 승인 후 implementer 재호출하여 마이그레이션 수정
4. reviewer 재검증 → PASS → 결과 보고
