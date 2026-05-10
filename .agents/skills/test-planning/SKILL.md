---
name: test-planning
description: Use when you have both a refined requirement artifact (acceptance criteria) and an API development plan (component inventory, task plan, API shape), and need to produce a structured test plan before implementation begins. This skill maps each acceptance criterion to concrete integration test cases that the implementation orchestrator can execute directly as code. Trigger this skill whenever the user says "테스트 플랜", "test plan", "테스트 항목 뽑아줘", or asks what tests should be written for a feature — especially if an api-development-plan output is already in context.
metadata:
  short-description: Derive a structured integration test plan from acceptance criteria and development plan
---

# Test Planning

## Purpose

- Turn acceptance criteria and a component-level development plan into a concrete,
  implementation-ready test plan.
- Map every acceptance criterion to at least one test case.
- Ensure the implementation orchestrator can write test code directly from this plan without
  re-deriving test scope or setup logic.

## Use When

- A `feature-requirement-refinement` output (Given/When/Then acceptance criteria) exists
- An `api-development-plan` output (component inventory, API shape, service flow) exists
- Implementation has not yet started

## Not Covered

- Writing the actual test code
- Modifying the development plan or acceptance criteria
- Verifying completed test results

## What to Read First

1. The acceptance criteria table from the requirement artifact
2. The component inventory and recommended flow from the development plan
3. **`.agents/guides/test-integration.md`** — the authoritative source for test structure, base
   class setup, fixtures, and constraints

## Planning Lens

- Every acceptance criterion should map to at least one test case. If an AC cannot be tested with
  the current component design, call it out explicitly.
- Derive test data setup requirements from the component inventory and service flow, not from the
  acceptance criteria alone.
- Identify which test cases require time manipulation and flag the target datetime explicitly.
- Keep the test class inventory minimal: group related acceptance criteria into one class unless
  they require meaningfully different setup.

## Steering

- Do not write Java code — this skill produces a plan, not an implementation.
- Do not produce generic test method names like `testSuccess` or `testFailure`. Every name should
  describe the behavior under test (e.g.,
  `getRanking_returnsSortedByScore_whenMultipleMembersExist`).
- Do not add test cases that are not grounded in an acceptance criterion or a real edge case from
  the component design.
- If an assertion for an AC is unclear, surface it as an open question rather than inventing one.
- Prefer the smallest fixture setup that exercises the target behavior.

## Completion Signals

- Every AC-ID is referenced by at least one test case.
- Every test case has a method name, HTTP call, setup requirement, and specific assertions.
- Time-sensitive test cases are flagged with an explicit frozen datetime.
- The test data summary is complete enough for `createTestData()` to be written without ambiguity.
- Any AC that cannot be covered is explained in Open Questions.

## Output Contract

The final response must include all sections below:

- `Test Scope Summary`
- `Test Class Inventory`
- `Test Cases`
- `Test Data Summary`
- `Open Questions`

## Response Template

```md
## Test Scope Summary

- Feature being tested:
- Total test classes:
- Total test cases:
- Acceptance criteria covered: X / Y
- Uncovered AC-IDs (if any):

## Test Class Inventory

| Class ID | Test Class Name | Feature Covered | AC-IDs Covered |
|---|---|---|---|
| TC-CLS-1 | ... | ... | AC-1, AC-2 |

## Test Cases

| TC-ID | Class | Method Name | AC-ID | HTTP Call | Setup Required | Assertions | setFixedClock |
|---|---|---|---|---|---|---|---|
| TC-1 | TC-CLS-1 | methodName_behavior_condition | AC-1 | GET /api/v1/... | testXyz saved with fieldA=v1 | status 200, body.field == expected | No |
| TC-2 | TC-CLS-1 | methodName_returns400_whenFieldMissing | AC-2 | POST /api/v1/... {body} | none beyond base | status 400, errorCode == E4XX | No |
| TC-3 | TC-CLS-1 | methodName_reflectsTimestamp | AC-3 | POST /api/v1/... | testXyz saved | status 200, body.createdAt == frozen | Yes — 2024-03-01T10:00 |

## Test Data Summary

- Fixture 1: [entity type], fields: fieldA=v1, fieldB=v2
- Fixture 2: ...
- Cleanup: deleteAll() per fixture repository

## Open Questions

- AC-N: [why it cannot be tested or what assertion is unclear]
```