# TechMoa

TechMoa App은 여러 기술 블로그의 RSS를 수집해 아티클을 통합 제공하고, 신규 글 발생 시 웹훅으로 알림을 전달하는 Kotlin + Spring Boot 기반 멀티모듈 백엔드 애플리케이션입니다.

## 핵심 기능

- 여러 기술 블로그 RSS를 수집하고 중복 없이 아티클을 저장합니다.
- 현재까지 약 1,000개의 블로그 아티클을 볼 수 있습니다. 
- 새로운 글이 등록될 때 Discord 웹훅으로 알림을 받을 수 있습니다.

## 멀티모듈 구조

```mermaid
graph TD
    boot["boot<br/>실행 진입점 / 설정 조합"]

    presentation["presentation<br/>REST API / 예외 처리 / 웹 설정"]
    application["application<br/>유스케이스 / 서비스 / 포트"]
    domain["domain<br/>도메인 모델 / 예외 / 이벤트"]

    batchRss["batch:rss<br/>RSS 수집 배치"]
    batchSchedules["batch:schedules<br/>신규 아티클 스캔 / 아웃박스 발행"]

    infraJpa["infrastructure:jpa<br/>JPA 어댑터 / 엔티티 / 리포지토리"]
    infraMysql["infrastructure:mysql<br/>MySQL / Flyway 마이그레이션"]
    infraRest["infrastructure:rest<br/>Webhook REST 발행"]

    presentation --> application
    presentation --> domain
    application --> domain

    infraJpa --> application
    infraJpa --> domain
    infraJpa -. runtime .-> infraMysql


    infraRest --> domain

    batchRss -. runtime .-> infraMysql
    batchSchedules --> domain
    batchSchedules -. runtime .-> infraMysql

    boot --> presentation
    boot --> application
    boot --> domain
    boot --> batchRss
    boot --> batchSchedules
    boot --> infraJpa
    boot --> infraMysql
    boot --> infraRest
```
