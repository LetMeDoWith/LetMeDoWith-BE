---
name: pr-body-from-branch
description: Draft a pull request body from the current Git branch by comparing it with the repository base branch and filling the repository's .github/PULL_REQUEST_TEMPLATE.md. Use when the agent needs to summarize branch changes, choose the relevant PR type checkboxes, capture issue or backlog references, and produce a review-ready PR description from commits, changed files, and diff context.
---

# PR Body From Branch

## Overview

- Read the repository PR template and produce a completed PR body from actual branch changes.
- Ground every statement in Git evidence. Do not invent tests run, Swagger work, issue links, or
  implementation details that are not visible in the repository or branch history.

## Workflow

### 1. Read the source of truth

- Read `.github/PULL_REQUEST_TEMPLATE.md` first. Always follow the current repository template
  instead of relying on a copied example.
- If the user names a different base branch, use it. Otherwise prefer `origin/develop` when it
  exists. If it does not exist, fall back to the remote HEAD branch, then `origin/main`, then
  `origin/master`.

### 2. Collect branch context

- Run `bash .agents/skills/pr-body-from-branch/scripts/collect_pr_context.sh`.
- Pass `--base <branch>` when the user requests a specific comparison target.
- The script may fetch remotes before comparing. If fetch fails, continue with local refs and
  explicitly say freshness was not verified.
- Use the script output as the first-pass summary, then open important diffs for files whose purpose
  is not obvious from names alone.

### 3. Inspect the actual change

- Review commits in chronological order.
- Review changed files and diff stats.
- Open representative diffs for controller, service, domain, DTO, repository, config, migration, or
  test files that materially explain the change.
- If the working tree is dirty, treat uncommitted changes as unverified draft content and mention
  that clearly before including them.

### 4. Fill the template conservatively

- Preserve the template headings and checkbox style.
- Mark only checklist items that are proven by the repo state or explicit user input.
- For `PR 타입`, check the boxes that materially match the change. Prefer one primary type, but mark
  multiple when the branch genuinely spans multiple categories.
- For `백로그 및 이슈 링크`, include an actual URL only when it is present in branch names, commit messages,
  or repository files. If only an issue key such as `TAS-495` is known, write the key plainly
  instead of fabricating a link.
- For `변경된 사항`, replace placeholder headings such as `사항1` with real subsection titles that
  summarize the change. Group related work into concise sections. Favor user-facing behavior, API
  contract changes, persistence changes, validation changes, and test additions over file-by-file
  narration.
- Use `기타 전달 사항` for risks, follow-up work, unverified items, reviewer attention points, or dirty
  working tree notes.

### 5. State uncertainty explicitly

- If you infer intent from code structure, say it is an inference.
- If tests were not run in this session, leave the checklist unchecked and mention that verification
  was not confirmed.
- If Swagger changes are not visible, leave that item unchecked.
- If the branch contains mixed concerns, call that out briefly instead of forcing an overconfident
  summary.

## Heuristics

- Branch naming:
    - `feature/*` usually maps to `신규 기능 개발` or `기능 수정`.
    - `fix/*` or `hotfix/*` usually maps to `버그 픽스`.
    - `refactor/*` usually maps to `리팩토링`.
    - Do not rely on branch name alone; confirm with the diff.
- Test-only or mostly-test changes usually map to `테스트 코드 추가`.
- Build file, Docker, CI, environment, or application config changes often require `설정 파일 수정`.
- Pure renames, formatting, or code style cleanup belong in `코드 스타일 업데이트` only when behavior is
  unchanged.

## Output Contract

- Return a complete Markdown PR body, ready to paste into GitHub.
- Keep the template's Korean headings unless the repository template changes.
- Keep bullets short and factual.
- Do not append a long analysis after the PR body unless the user explicitly asks for rationale.

## Phase 5: Post PR to GitHub (optional)

사용자가 PR 본문을 승인하면 `gh` CLI로 PR을 게시한다.

### 사전 확인

`gh auth status`를 실행한다. 인증이 되어 있지 않으면 멈추고 사용자에게 다음을 알린다:

> `gh` CLI가 GitHub에 인증되어 있지 않습니다. 아래 명령으로 로그인 후 다시 시도해주세요:
> ```bash
> gh auth login
> ```

### PR 게시 절차

1. 승인된 PR 본문을 `.github/PULL_REQUEST_TEMPLATE.md` 구조 그대로 임시 파일에 저장한다:
   ```
   .agents/output/pr-body-{branch-sanitized}.md
   ```
   브랜치명의 `/`는 `-`로 치환한다. 예: `feature/TAS-495` → `pr-body-feature-TAS-495.md`

2. PR을 게시한다:
   ```bash
   gh pr create \
     --title "<PR 제목>" \
     --base develop \
     --body-file .agents/output/pr-body-{branch-sanitized}.md
   ```
   - 드래프트로 올리려면 `--draft` 플래그를 추가한다.
   - `--base`는 보통 `develop`. 사용자가 다른 브랜치를 지정하면 그것을 사용한다.

3. 명령이 성공하면 출력된 PR URL을 사용자에게 전달한다.

4. 임시 파일을 삭제한다:
   ```bash
   rm .agents/output/pr-body-{branch-sanitized}.md
   ```

**오류 대응:**

- `gh: command not found` → 사용자에게 알린다: "`gh` CLI가 설치되어 있지 않습니다. `brew install gh` 후 `gh auth login`을 실행해주세요."
- `gh auth` 오류 → 사용자에게 알린다: "GitHub 인증이 만료되었거나 없습니다. `gh auth login`을 실행해주세요."
- `A pull request already exists` → 해당 브랜치에 이미 PR이 존재함. `gh pr view` 로 확인

## Command Reference

- Default context collection:

```bash
bash .agents/skills/pr-body-from-branch/scripts/collect_pr_context.sh
```

- Compare against a specific base:

```bash
bash .agents/skills/pr-body-from-branch/scripts/collect_pr_context.sh --base origin/develop
```

- Post PR to GitHub:

```bash
gh pr create \
  --title "<PR 제목>" \
  --base develop \
  --body-file .agents/output/pr-body-{branch-sanitized}.md
```
