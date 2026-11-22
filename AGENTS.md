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
- 배포 성공을 확인한 뒤 배포에 포함된 열린 하위 이슈를 완료 처리하고 릴리스 이슈를 마지막에 닫는다. 이미 닫힌 하위 이슈는 연결을 유지한다. 배포 실패 시 릴리스 이슈를 열어 둔다.
