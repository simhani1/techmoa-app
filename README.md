# TechMoa

TechMoa App은 여러 기술 블로그의 RSS를 수집해 아티클을 통합 제공하고, 신규 글 발생 시 웹훅으로 알림을 전달하는 Kotlin + Spring Boot 기반 멀티모듈 백엔드 애플리케이션입니다.

## 핵심 기능

- 여러 기술 블로그 RSS를 가상 스레드로 동시에 수집하고 중복 없이 아티클을 저장합니다.
- 현재까지 약 1,000개의 블로그 아티클을 볼 수 있습니다.
- 카카오 OAuth로 회원가입·로그인할 수 있습니다.
- 웹훅(Discord)을 등록하면 이후 수집된 새 글을 MySQL Outbox를 거쳐 직접 발송합니다. 개별·전역 429 제한이 풀리면 발송을 재개합니다.
- Loki + Grafana로 RSS 수집 결과와 알림 발송 성공 건수를 모니터링합니다.

## 알림 예시

새 글이 등록되면 아래처럼 Discord 채널로 알림이 전달됩니다.

![Discord 알림 예시](docs/images/discord-notification.png)

## 멀티모듈 구조

```mermaid
graph TD
    boot["boot<br/>실행 진입점 / 설정 조합"]

    presentation["presentation<br/>REST API / 예외 처리 / 웹 설정"]
    application["application<br/>유스케이스 / 서비스 / 포트"]
    domain["domain<br/>도메인 모델 / 예외 / 이벤트"]

    workerRss["worker:rss<br/>RSS 동시 수집"]
    workerScheduler["worker:scheduler<br/>신규 아티클 스캔 / Outbox 발송"]

    infraJpa["infrastructure:jpa<br/>JPA 어댑터 / 엔티티 / 리포지토리"]
    infraMysql["infrastructure:mysql<br/>MySQL / Flyway 마이그레이션"]
    infraOauth["infrastructure:oauth<br/>카카오 OAuth 어댑터"]
    infraRest["infrastructure:rest<br/>Discord 웹훅 발송"]

    presentation --> application
    presentation --> domain
    application --> domain

    infraJpa --> application
    infraJpa --> domain
    infraJpa -. runtime .-> infraMysql
    infraOauth --> application
    infraOauth --> domain
    infraRest --> domain

    workerRss -. runtime .-> infraMysql
    workerScheduler --> domain
    workerScheduler -. runtime .-> infraMysql

    boot --> presentation
    boot --> application
    boot --> domain
    boot --> workerRss
    boot --> workerScheduler
    boot --> infraJpa
    boot --> infraMysql
    boot --> infraOauth
    boot --> infraRest
```

## 모니터링

RSS 수집 시간·실패 블로그·최근 24시간 발송 성공 건수를 Grafana 대시보드로 확인합니다. 설치 절차와 패널 해석은 [ops/rss-monitoring](ops/rss-monitoring/README.md)에 있습니다.

![Grafana RSS 수집 대시보드](docs/images/grafana-rss.png)

![Grafana 알림 발송 및 JVM 대시보드](docs/images/grafana-delivery-jvm.png)
