---
name: api-development-plan
description: Use when you are given API requirements and need to derive a development plan before coding. Analyze nearby API implementations to identify required components, likely file changes, and the responsibilities each component should own.
metadata:
  short-description: Produce an API implementation plan from requirements
---

# API Development Plan

## Purpose
- Act as a designer before implementation starts.
- Turn API requirements into a code-grounded development plan.
- Identify what needs to be built, which components are needed, what each component should do, and in what order the work should proceed.

## Use When
- A new API needs to be designed before code changes begin
- Existing API requirements need to be translated into a concrete implementation plan
- You need a handoff artifact for an implementer or subagent

## Not Covered
- Writing the implementation
- Writing or planning tests
- Verifying completed code changes
- Executing parallel work; this skill only proposes safe grouping candidates for future execution

## Source of Truth
- Use code, configuration, and adjacent implementations as the primary references.
- Prefer nearby controllers, DTOs, services, exception handling, auth flow, and repository usage over prose documents.
- If requirements and existing code appear to conflict, call out the mismatch explicitly instead of inventing a hidden assumption.

## Planning Lens
- Treat the requirement as a design brief, not a direct coding instruction.
- Infer the intended implementation style from similar APIs that already exist in the codebase.
- Prefer extending existing component patterns over introducing new layers or abstractions.
- Produce a plan that is specific enough for implementation, but grounded enough that another agent can verify why each part exists.
- When direct precedent is weak, fall back to stable repository-wide architecture patterns and the refined requirement artifact instead of guessing from thin examples.
- In low-precedent areas, make the proposed pattern explicit so it can become a reusable reference for future work.

## What to Reference
- Find 2-3 existing APIs that are closest in responsibility, request shape, response shape, or domain behavior.
- Read the surrounding controller, request DTO, response DTO, service flow, and exception handling for those references.
- If the requirement implies persistence or external calls, inspect the related repository, entity, gateway, or client patterns only as far as needed.
- Pay attention to whether the codebase splits read and write concerns across query repositories, command repositories, domain repository interfaces, and infrastructure implementations in different packages.
- Note existing auth, validation, and common response wrapper patterns if they affect the requested API.
- Read the relevant layer guides before proposing component structure:
  - Overall package and repository structure → `.agents/guides/architecture.md`
  - Domain entities, aggregates, domain services → `.agents/guides/layer-domain.md`
  - Application services, Command/Result DTOs → `.agents/guides/layer-application.md`
  - Repository implementations, QueryDSL → `.agents/guides/layer-infrastructure.md`
  - Controllers, response format, error codes → `.agents/guides/layer-presentation.md`
  - Batch jobs or schedulers → `.agents/guides/batch.md`

## What to Derive
- The component inventory needed to satisfy the requirement
- The responsibility of each component
- The development tasks needed to satisfy the requirement
- The recommended task order
- Which tasks are sequentially dependent and which look parallelizable
- The persistence or repository work implied by the requirement
- Whether the work belongs to a query path, a command path, or both
- The expected request and response shape
- The likely service flow and dependency touchpoints
- Any constraints inherited from existing patterns
- Any open questions that block confident implementation

## Steering
- Do not jump into coding.
- Do not present implementation guesses without tying them back to code references.
- Do not generate a generic checklist that could apply to any project.
- Keep the plan scoped to the stated requirement.
- If multiple implementation directions are plausible, present the recommended one first and explain why it fits the codebase best.
- Break the work into concrete tasks that an implementer or subagent could pick up without re-deriving the design.
- Group only tasks that appear parallelizable from the current evidence; if sequencing is unclear, keep the tasks sequential.
- If feature-specific precedent is weak, use repository-local evidence in this order:
  - Stable architecture patterns already used across the codebase
  - Refined requirements and acceptance criteria
  - The closest adjacent implementations, even if they are not exact matches
- In weak-precedent areas, prefer the smallest consistent pattern that can serve as a future reference.
- When existing code supports it, prefer a DDD-lite split:
  - Put core business logic in domain entities by default.
  - Use application services for orchestration and transaction boundaries.
  - Use domain services for business rules that do not fit cleanly inside a single entity.
- Do not force this split if nearby code uses a different established pattern; call out the mismatch instead.
- Call out separately when a domain repository interface, an infrastructure repository implementation, and a lower-level JPA or query repository are all part of the likely change surface.

## Completion Signals
- You can name the reference implementations that shaped the plan.
- You can list the concrete components required for the API.
- You can list the concrete development tasks required for the API.
- You can explain the recommended task sequence.
- You can identify which tasks appear safe to parallelize and why.
- You can explain which components should be created or modified.
- You can describe each component's responsibility in the proposed design.
- You can identify the repository or persistence changes required by the design.
- You can explain whether the plan is following an established precedent or proposing a minimal new precedent for similar future work.
- You can hand the output to an implementer without needing to restate the requirement.

## Output Contract
The final response must include all sections below:
- `Requirement Summary`
- `References`
- `Design Decisions`
- `Component Inventory`
- `Development Task Plan`
- `Parallelization Groups`
- `Recommended Flow`
- `Constraints`
- `Open Questions`

## Template
- Use the output template at `references/api-development-plan-template.md`.
- Fill every section in the template.
- Keep task IDs stable and explicit so later implementation steps can refer to them directly.
- For `Parallelization Groups`, group tasks only when the current plan indicates they can be worked independently with low coupling.
