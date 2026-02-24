# API 구현 문서 (Spring Boot)

> 기준: 현재 소스 기준 (`presentation`, `application`, `infrastructure` 모듈)

## 공통 응답 포맷

모든 API 응답은 공통 형태입니다.

```json
{
  "resultType": "SUCCESS|ERROR",
  "data": ...,
  "errorMessage": {
    "code": "<ERROR_CODE>",
    "message": "<클라이언트 노출 메시지>"
  }
}
```

- 성공 시: `resultType=SUCCESS`, `errorMessage`는 `null`
- 실패 시: `resultType=ERROR`, `data`는 `null`
- 에러 코드는 `ErrorType`의 `code`
- 에러 핸들링은 `GlobalExceptionHandler`가 `DomainException`만 처리
  - 그 외의 바인딩/검증 오류(`400`) 등은 컨트롤러 레벨 전/후 처리 경로에서 스프링 기본 에러 응답이 나올 수 있습니다.
  - `DomainException`이 아닌 `RuntimeException`은 기본적으로 500으로 처리될 가능성이 있습니다.

`ResultType`: `presentation/common/template/ResultType.kt`
`ApiResponse`: `presentation/common/template/ApiResponse.kt`

## 공통 에러 코드 매핑

| ErrorType | HTTP | code | message |
|---|---:|---|---|
| INVALID_REQUEST | 400 | BAD_REQUEST | 요청이 올바르지 않습니다. |
| NOT_FOUND_DATA | 404 | NOT_FOUND_DATA | 해당 데이터를 찾을 수 없습니다. |
| KAKAO_CLIENT_ERROR | 400 | KAKAO_CLIENT_ERROR | 카카오 인증 요청이 올바르지 않습니다. |
| KAKAO_SERVER_ERROR | 502 | KAKAO_SERVER_ERROR | 카카오 인증 서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요. |
| KID_NOT_MATCH | 401 | KID_NOT_MATCH | OIDC 토큰 검증에 실패했습니다. |
| DUPLICATED_WEBHOOK | 400 | DUPLICATED_WEBHOOK | 이미 등록된 웹훅입니다. |
| INVALID_WEBHOOK_PLATFORM | 400 | INVALID_WEBHOOK_PLATFORM | 지원하지 않는 웹훅 플랫폼입니다. |
| INVALID_WEBHOOK_URL | 400 | INVALID_WEBHOOK_URL | 웹훅 URL 형식이 올바르지 않습니다. |
| DEFAULT_ERROR | 500 | INTERNAL_SERVER_ERROR | 알 수 없는 오류가 발생했습니다. 잠시 후 다시 시도해주세요. |

---

## 1) GET `/v1/articles`

### 설명
발행된 기사 목록 조회 (커서 기반 페이징)

### 요청

- Method: `GET`
- Path: `/v1/articles`
- Query
  - `cursor` (optional, `Long`)
  - page size: 서버 고정 `20` (`ArticleControllerV1.PAGE_LIMIT`)

#### 예시

```http
GET /v1/articles?cursor=1700000000000 HTTP/1.1
Host: api.example.com
```

```http
GET /v1/articles HTTP/1.1
Host: api.example.com
```

### 응답

#### 200 Success

```json
{
  "resultType": "SUCCESS",
  "data": {
    "data": [
      {
        "article": {
          "id": 101,
          "blogId": 3,
          "title": "Techmoa Weekly",
          "link": "https://blog.example.com/posts/101",
          "guid": "guid-101",
          "pubDate": 1700000000000,
          "views": 57
        },
        "blog": {
          "id": 3,
          "link": "https://blog.example.com",
          "name": "Example Blog",
          "logoUrl": "https://cdn.example.com/logo.png",
          "rssLink": "https://blog.example.com/rss.xml"
        }
      }
    ],
    "hasNext": true,
    "nextCursor": 1699000000000
  },
  "errorMessage": null
}
```

#### 비즈니스 예외
현재 본 엔드포인트 경로 상 비즈니스 예외(`DomainException`)를 직접 던지지 않음
- 기본 동작은 `200` 또는(예외 미처리 시) 시스템 500 가능성

### 프론트 연동 포인트
- `nextCursor`가 null 이면 마지막 페이지로 간주
- 다음 요청은 `GET /v1/articles?cursor={nextCursor}`
- 응답은 항상 `data.data` 내부가 실제 목록임을 주의

---

## 2) GET `/v1/articles/{articleId}`

### 설명
단일 기사 상세 조회

### 요청

- Method: `GET`
- Path: `/v1/articles/{articleId}`
- Path Variable: `articleId` (`Long`)

#### 잘못된 값 예시

```http
GET /v1/articles/abc HTTP/1.1
Host: api.example.com
```

#### 예시

```http
GET /v1/articles/101 HTTP/1.1
Host: api.example.com
```

### 응답

#### 200 Success

```json
{
  "resultType": "SUCCESS",
  "data": {
    "article": {
      "id": 101,
      "blogId": 3,
      "title": "Techmoa Weekly",
      "link": "https://blog.example.com/posts/101",
      "guid": "guid-101",
      "pubDate": 1700000000000,
      "views": 57
    },
    "blog": {
      "id": 3,
      "link": "https://blog.example.com",
      "name": "Example Blog",
      "logoUrl": "https://cdn.example.com/logo.png",
      "rssLink": "https://blog.example.com/rss.xml"
    }
  },
  "errorMessage": null
}
```

#### 404 Not Found

```json
{
  "resultType": "ERROR",
  "data": null,
  "errorMessage": {
    "code": "NOT_FOUND_DATA",
    "message": "해당 데이터를 찾을 수 없습니다."
  }
}
```

### 예외 케이스

- `BAD_REQUEST` (400, 바인딩 실패)
  - 조건: `articleId`가 숫자(Long)로 파싱되지 않음 (`abc` 등)
  - 매핑: `DomainException`이 아니므로 `INVALID_REQUEST` 에러 매핑이 적용되지 않을 수 있음
  - 보통 스프링 바인딩 예외(400) 응답이 반환됨

- `NOT_FOUND_DATA` (404)
  - 조건: 기사 없음
    - 로그/메시지 예시: `Article not found with id: {articleId}`
  - 매핑: `ErrorType.NOT_FOUND_DATA`

- `NOT_FOUND_DATA` (404)
  - 조건: 기사 조회 후 연결된 블로그가 없음
    - 메시지 예시: `Blog not found with id: {blogId}`
  - 매핑: `ErrorType.NOT_FOUND_DATA`

---

## 3) POST `/v1/articles/{articleId}`

### 설명
특정 기사 조회수 1 증가

### 요청

- Method: `POST`
- Path: `/v1/articles/{articleId}`
- Path Variable: `articleId` (`Long`)

#### 잘못된 값 예시

```http
POST /v1/articles/abc HTTP/1.1
Host: api.example.com
```

#### 예시

```http
POST /v1/articles/101 HTTP/1.1
Host: api.example.com
```

### 응답

#### 200 Success

```json
{
  "resultType": "SUCCESS",
  "data": null,
  "errorMessage": null
}
```

#### 404 Not Found

```json
{
  "resultType": "ERROR",
  "data": null,
  "errorMessage": {
    "code": "NOT_FOUND_DATA",
    "message": "해당 데이터를 찾을 수 없습니다."
  }
}
```

### 예외 케이스

- `BAD_REQUEST` (400, 바인딩 실패)
  - 조건: `articleId`가 숫자(Long)로 파싱되지 않음 (`abc` 등)
  - 매핑: `DomainException`이 아니므로 `INVALID_REQUEST` 에러 매핑이 적용되지 않을 수 있음
  - 보통 스프링 바인딩 예외(400) 응답이 반환됨

- `NOT_FOUND_DATA` (404)
  - 조건: 대상 기사 없음
  - 매핑: `ErrorType.NOT_FOUND_DATA`

### 프론트 연동 포인트
- 동일 기사 동시 요청이 들어와도 현재 구현은 단순 증가 처리(멱등이 아님)
- 연동 시 `204` 대신 `200` 기반으로 처리

---

## 4) GET `/v1/oauth/kakao/callback?code={code}`

### 설명
카카오 OAuth 로그인 콜백. 코드 수신 후 내부 토큰 발급과 유저 등록/조회 후 쿠키 발급

### 요청

- Method: `GET`
- Path: `/v1/oauth/kakao/callback`
- Query
  - `code` (required, string)

#### 예시

```http
GET /v1/oauth/kakao/callback?code=abc123xyz HTTP/1.1
Host: api.example.com
```

### 응답

#### 200 Success

```http
HTTP/1.1 200 OK
Set-Cookie: accessToken=<JWT>; Path=/; HttpOnly; Max-Age=3600; Secure
Content-Type: application/json
```

```json
{
  "resultType": "SUCCESS",
  "data": null,
  "errorMessage": null
}
```

#### 에러 예시

```json
{
  "resultType": "ERROR",
  "data": null,
  "errorMessage": {
    "code": "KAKAO_CLIENT_ERROR",
    "message": "카카오 인증 요청이 올바르지 않습니다."
  }
}
```

```json
{
  "resultType": "ERROR",
  "data": null,
  "errorMessage": {
    "code": "KAKAO_SERVER_ERROR",
    "message": "카카오 인증 서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요."
  }
}
```

```json
{
  "resultType": "ERROR",
  "data": null,
  "errorMessage": {
    "code": "KID_NOT_MATCH",
    "message": "OIDC 토큰 검증에 실패했습니다."
  }
}
```

```json
{
  "resultType": "ERROR",
  "data": null,
  "errorMessage": {
    "code": "BAD_REQUEST",
    "message": "요청이 올바르지 않습니다."
  }
}
```

### 예외 케이스

- `KAKAO_CLIENT_ERROR` (400)
  - 조건: 카카오 토큰 발급 API에서 4xx 응답

- `KAKAO_SERVER_ERROR` (502)
  - 조건: 카카오 토큰 발급 API에서 5xx 응답

- `BAD_REQUEST` (400)
  - 조건: 토큰 형식 파싱/유효성 검증 실패 (`InvalidTokenException`)
  - 예: ID token part 수가 3이 아님, kid 미존재 등

- `KID_NOT_MATCH` (401)
  - 조건: OIDC `kid`가 공개키 목록과 불일치

- 500 `INTERNAL_SERVER_ERROR`
  - 조건: 내부에서 처리되지 않은 런타임 오류(예: OIDC 구현 내부의 `RuntimeException("mma")` 경로)

### 프론트 연동 포인트
- 로그인 응답 본문은 비어 있어도 정상 응답은 `Set-Cookie` 헤더에서 토큰을 사용
- 쿠키는 `HttpOnly`로 내려오므로 JS에서 접근 불가
- `code`는 URL 디코딩 가능한 값이어야 함(쿼리스트링 인코딩 필요 시 백엔드 쿼리 인코딩 규칙 준수)

---

## 5) POST `/v1/webhooks`

### 설명
웹훅 등록

### 요청

- Method: `POST`
- Path: `/v1/webhooks`
- Headers: `Content-Type: application/json`
- Body
  - `platform`: string
  - `url`: string

#### 허용값

- `platform`은 `WebhookPlatform` enum에 정의된 값만 허용됩니다.
  - `discord` (`DISCORD`)
  - `slack` (`SLACK`)
- 요청 값은 `trim()` 후 대문자 변환(`uppercase`)되어 `WebhookPlatform.valueOf(...)`로 매핑되므로, 대소문자 구분 없이 입력 가능합니다.
- 허용되지 않는 값은 `INVALID_WEBHOOK_PLATFORM`(400) 응답입니다.

#### 요청 바디 예시

```json
{
  "platform": "discord",
  "url": "https://discord.com/api/webhooks/abc/xyz"
}
```

#### 예시

```http
POST /v1/webhooks HTTP/1.1
Host: api.example.com
Content-Type: application/json

{
  "platform": "discord",
  "url": "https://discord.com/api/webhooks/abc/xyz"
}
```

### 응답

#### 200 Success

```json
{
  "resultType": "SUCCESS",
  "data": {
    "webhookId": 123
  },
  "errorMessage": null
}
```

#### 400 Bad Request (중복)

```json
{
  "resultType": "ERROR",
  "data": null,
  "errorMessage": {
    "code": "DUPLICATED_WEBHOOK",
    "message": "이미 등록된 웹훅입니다."
  }
}
```

#### 400 Bad Request (플랫폼 미지원)

```json
{
  "resultType": "ERROR",
  "data": null,
  "errorMessage": {
    "code": "INVALID_WEBHOOK_PLATFORM",
    "message": "지원하지 않는 웹훅 플랫폼입니다."
  }
}
```

#### 400 Bad Request (URL 형식 오류)

```json
{
  "resultType": "ERROR",
  "data": null,
  "errorMessage": {
    "code": "INVALID_WEBHOOK_URL",
    "message": "웹훅 URL 형식이 올바르지 않습니다."
  }
}
```

### 예외 케이스

- `DUPLICATED_WEBHOOK` (400)
  - 조건: 동일 플랫폼+URL로 이미 등록됨

- `INVALID_WEBHOOK_PLATFORM` (400)
  - 조건: `platform` 값이 enum(`discord` 등 허용값)으로 매핑되지 않음

- `INVALID_WEBHOOK_URL` (400)
  - 조건: `url`이 공백/빈 문자열

### 프론트 연동 포인트
- `platform` 허용값 표를 기준으로 UI에서 드롭다운(예: `discord`, `slack`)을 고정 렌더링하세요.
- UI 전송 전 `platform`을 소문자(`discord`, `slack`)로 정규화해 보내면 불필요한 400을 줄일 수 있습니다.
- URL은 trim 후 검증되므로, 프론트 유효성 검사도 trim 기준으로 맞추세요.

---

## 6) GET `/v1/blogs`

### 설명
활성 블로그 전체 목록 조회

### 요청

- Method: `GET`
- Path: `/v1/blogs`

#### 예시

```http
GET /v1/blogs HTTP/1.1
Host: api.example.com
```

### 응답

#### 200 Success

```json
{
  "resultType": "SUCCESS",
  "data": [
    {
      "order": 1,
      "link": "https://blog-a.example.com",
      "name": "A Blog"
    },
    {
      "order": 2,
      "link": "https://blog-b.example.com",
      "name": "B Blog"
    }
  ],
  "errorMessage": null
}
```

### 정렬/필드 규칙

- `name` 오름차순으로 정렬되어 응답됩니다.
- `order`는 정렬된 결과의 순번이며 현재 구현은 `1`부터 시작합니다.

### 예외 케이스

- 현재 본 엔드포인트 경로 상 비즈니스 예외(`DomainException`)를 직접 던지지 않음
- 기본 동작은 `200` 또는(예외 미처리 시) 시스템 500 가능성

### 프론트 연동 포인트

- `order`는 이미 1-based 순번으로 내려오므로 프론트에서 별도 보정 없이 표시할 수 있습니다.
- 서버 정렬 기준은 `name` 기준이므로, 추가 정렬이 필요하면 클라이언트 정렬 정책을 명시적으로 분리하세요.

---

## 연동 체크리스트 (프론트 우선)

- `GET /v1/articles`는 `nextCursor` 기준으로 다음 페이지를 호출
- 에러 시 응답은 항상 HTTP 코드만 보는 대신 `resultType=ERROR`와 `errorMessage.code`를 함께 판별
- OAuth 성공 응답은 바디보다 `Set-Cookie` 헤더의 존재를 우선 확인
- `/v1/webhooks`의 유효성 실패는 요청 400+`errorMessage.code`로 즉시 사용자 피드백
- `/v1/blogs`의 `order`는 1-based 순번으로 내려오므로 그대로 표시 가능
- `NOT_FOUND_DATA`는 대부분 경로 파라미터(`articleId`) 잘못/삭제된 데이터 케이스로 처리

## 파일 위치

문서 파일: `docs/api.md`
