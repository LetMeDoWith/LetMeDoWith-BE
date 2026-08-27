---
name: local-server-bootstrap
description: Use when a local Spring server must be started from this repository using .env-backed environment variables, Docker-backed local dependencies, health checks, and optional local DB recovery.
---

# Local Server Bootstrap

Use this skill to prepare the local server for API testing.

## Do

- Read `.env` and use it as the source of local environment variables unless the user explicitly overrides them.
- Confirm required local dependencies such as Docker MySQL and Redis are available when the app expects them.
- Start the server with the repository's local run command.
- Verify startup with `/health-check`.

## Failure handling

- If startup fails, inspect the error briefly.
- If the failure is caused by local DB schema drift or stale Flyway state, use `local-db-reset`.
- After reset, start the server again and re-run the health check.

## Rules

- Default to local profile only.
- Do not reset the DB preemptively.
- Do not touch non-local environments.
- Keep the verification narrow: boot logs plus health check.

## Report

Include:

- whether the server started
- whether DB reset was needed
- health check result
