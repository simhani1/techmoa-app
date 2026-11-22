# AGENTS

## Scope
- This file defines default working rules for `/Users/simjonghan/source_code/techmoa/app`.

## Testing Prompt Rule
- When the user asks to write, review, or modify tests, read `prompts/TEST_REQUEST_TEMPLATE.md` first.
- Follow the template sections as the default checklist: scope, stack, layer rules, validation points, fixture rules, and done criteria.
- If required template fields are missing, ask concise follow-up questions before implementing tests.

## Release tracking
- 새 작업 이슈를 등록할 때 대상 버전의 릴리스 이슈를 GitHub의 부모 이슈로 지정한다. 버전이 정해지지 않았다면 먼저 대상 버전을 확인한다.
- 배포 전에는 릴리스 PR의 변경 사항과 하위 이슈를 대조한다. 미포함 작업은 다음 대상 릴리스로 옮긴다.
- 릴리스 PR 본문에 `Closes #릴리스이슈번호`를 적는다. main 병합 직후 포함된 하위 이슈와 릴리스 이슈의 종료 상태를 확인하고, 열려 있으면 해당 PR을 참조해 닫는다.
- 배포 결과는 병합과 별도로 확인하고 실패 시 배포 실행 링크를 기록한다.
