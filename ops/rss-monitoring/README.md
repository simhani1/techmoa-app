# RSS와 알림 발송 모니터링

운영 Spring 앱이 전용 Logback 로거의 이벤트를 Loki4j로 비동기 전송한다. Loki는 Docker 소켓 접근 없이 데이터를 7일간 저장한다. 앱과 Grafana가 연결된 내부 monitor-net을 사용한다.

1. 홈서버에서 이 디렉터리의 `docker compose up -d`로 Loki를 시작한다. 이전 PR 초안의 Alloy를 실행 중이라면 동일한 Compose 프로젝트에서 `docker compose up -d --remove-orphans`로 교체한다.
2. 이 변경이 담긴 앱을 prod 프로필로 배포한다. 기본 전송 주소는 `http://loki:3100/loki/api/v1/push`이고 앱 컨테이너 환경변수 LOKI_PUSH_URL로 변경할 수 있다. 운영 지표 이벤트만 전송하며 로그에 웹훅 URL이나 토큰을 넣지 않는다.
3. Grafana의 **Connections → Data sources**에서 Loki URL을 `http://loki:3100`으로 설정한다. **Dashboards → New → Import**에서 dashboard.json을 올리고 해당 데이터 소스를 선택한다. 이전 버전을 임포트했다면 같은 UID로 갱신한다.

| 패널 | 해석 |
|---|---|
| 실패 블로그 | 선택한 기간의 블로그별 실패 횟수. 회복된 블로그도 기간 내 실패가 있으면 표시된다. |
| 피드별 평균 수집 시간 | 각 HTTP 요청과 피드 파싱이 끝날 때까지 걸린 시간의 산술 평균(ms). 실패한 시도도 포함하며 DB 저장 시간은 제외한다. |
| 회차별 결과 | 수집 회차별 전체·성공·실패 피드 수. |
| 최근 24시간 발송 성공 | 조회 종료 시각 직전 24시간 동안 Outbox에 성공을 기록한 발송 요청 수. 같은 글을 3개 웹훅에 보내면 3건이다. |

Loki 장애나 버퍼 포화 시 운영 로그가 유실될 수 있어 대시보드는 관측된 건수를 보여준다. 성공 로그가 없으면 0과 수집 중단을 구분할 수 없어 데이터 없음으로 표시한다. 앱 재시작 후 과거 Outbox를 다시 집계하지 않는다.
