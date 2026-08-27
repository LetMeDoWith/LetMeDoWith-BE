---
name: local-api-smoke-test
description: Use when testing repository APIs end-to-end on a local server with real HTTP calls. Starts the local server if needed, gets a dev temp access token, performs request-specific setup, runs the target API flow, and reports actual request and response results.
---

# Local API Smoke Test

Use this skill for local end-to-end API verification.

## Do

- Treat this as the main entry skill.
- First use `local-server-bootstrap`.
- Then use `local-dev-auth`.
- After that, decide the remaining setup and API sequence based on the user's target scenario.
- Execute real HTTP requests against the local server.
- Report actual HTTP status codes, important response fields, and mismatches between API contract and runtime behavior.

## Common scope

- Step 0: start local server
- Step 1: issue dev temp ATK
- Step 2 and later: scenario-specific setup and verification

## Rules

- Read code for the target API before calling it.
- Prefer the smallest setup needed for the requested test.
- If the local server fails because of DB schema state, rely on `local-server-bootstrap` to decide whether `local-db-reset` is needed.
- Do not claim success without real execution.

## Report

Include:

- endpoints called
- request order
- key request bodies or headers when relevant
- HTTP status codes
- root cause if something fails
