# RSS 수집 모니터링

홈서버의 기존 Grafana와 techmoa-app이 사용하는 monitor-net에 Alloy와 Loki를 연결한다. Alloy가 Docker API로 techmoa-app의 stdout/stderr를 읽고 블로그별 수집 실패와 회차별 수집 요약 두 종류의 로그만 Loki로 보낸다. Loki 보관 기간은 7일이다.

1. 홈서버에 이 디렉터리를 복사하고 여기서 `docker compose up -d`를 실행한다. 기존 Grafana와 앱 컨테이너는 재생성할 필요가 없다. Docker 소켓을 사용할 수 있는 Alloy는 신뢰할 수 있는 관리자만 설정해야 한다. Loki는 호스트 포트를 공개하지 않으며 monitor-net 내부 접근을 전제로 한다.
2. Grafana의 **Connections → Data sources → Add data source → Loki**에서 URL을 `http://loki:3100`으로 설정한 후 **Save & test**를 누른다.
3. **Dashboards → New → Import**에서 `dashboard.json`을 업로드하고 위 Loki 데이터 소스를 선택한다.
4. Explore에서 `{job="techmoa-rss"}`를 조회한다. 운영 수집 주기는 30분이다. 회차별 로그가 들어오는데 실패 표만 비어 있으면 해당 조회 기간에 관측된 실패가 없는지 확인한다. 두 패널이 모두 비어 있으면 `docker compose logs --tail 50 alloy loki`로 수집 경로를 점검한다.

표는 선택 기간의 실패 횟수를 블로그별로 합산한다. 회복된 블로그도 기간 내에 실패했으면 표시된다. 실패에는 주소 변경 외에 타임아웃과 XML 파싱 오류도 포함된다. 회차별 패널은 최신순으로 전체·성공·실패 건수를 보여준다.
