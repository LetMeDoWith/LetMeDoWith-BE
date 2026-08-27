---
name: local-db-reset
description: Use when the local Docker-backed test database must be safely reset because schema validation, Flyway history, or local migration state is broken during local server startup or smoke testing.
---

# Local DB Reset

Use this skill only for local DB recovery during testing.

## Do

- Confirm the target is a local Docker MySQL database.
- Read `.env` and local Docker configuration as needed.
- Drop and recreate only the intended local schema.
- Restore required privileges if needed.
- Verify the schema is reset to an empty or expected pre-start state.

## Safety rules

- Local only.
- Docker MySQL only.
- Never use this for dev, staging, or prod hosts.
- Only run when startup failure or DB state clearly justifies it.

## Report

Include:

- database or schema reset
- why reset was needed
- verification after reset
