# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
./gradlew bootRun                                          # Run application
./gradlew test                                             # Run all tests
./gradlew test --tests "<FullyQualifiedClassName>"         # Run single test class
./gradlew build                                            # Build
```

## Ground Rules

- Read the relevant guide(s) below before starting any task. The guides are the harness — follow them.
- Read existing code in the same domain before writing new code. Match its patterns exactly.
- Keep changes scoped to the request. Do not add abstraction, utilities, or helpers beyond what the task requires.
- Do not add defensive code, null checks, or error handling for cases that cannot occur.
- Do not report verification as done unless it was actually run.
- Explicitly mention assumptions, unverified areas, and likely impact in the final report.

## Git Workflow

- Always branch from `develop`. Update local `develop` before branching.
- Branch naming: `feature/<ticket-name>`
- Use `git worktree` to preserve the current branch when switching to another task.

```bash
git fetch origin
git checkout develop && git pull --rebase origin develop
git checkout -b feature/<ticket-name>
```

## Local Environment

- Local app testing assumes Docker-backed MySQL and Redis.
- Use the local profile for smoke tests unless the request says otherwise.

## Skills

Small and obvious changes may be handled directly without invoking a skill.

- `$api-development-plan`: for API requirements that need implementation planning before coding.
- `$feature-requirement-refinement`: for rough feature requests that need structured requirements first.
- `$pr-body-from-branch`: for drafting a PR body from real branch diff context.
- `$local-api-smoke-test`: for real local end-to-end API execution.
- `$local-server-bootstrap`: for booting the local server and recovering local startup issues.
- `$local-dev-auth`: for issuing a local dev temp access token.
- `$local-db-reset`: only for justified local Docker MySQL reset during smoke testing.
