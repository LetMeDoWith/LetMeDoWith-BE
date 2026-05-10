---
name: implementation-orchestration
description: Use when you already have a structured development plan and need to orchestrate implementation across sequential dependency groups and parallelizable task groups. Coordinate subagents, collect results, write machine-readable handovers between groups, and produce a final integrated implementation report.
metadata:
  short-description: Orchestrate grouped implementation from a structured plan
---

# Implementation Orchestration

## Purpose
- Turn a structured development plan into an orchestrated implementation workflow.
- Help the main agent decide what to execute sequentially, what can be parallelized, and what must be handed over between groups.
- Ensure each later group can focus on its own scope by consuming machine-readable handover artifacts instead of re-deriving the whole context.

## Use When
- A development plan already exists with task IDs, component inventory, and parallelization groups
- The work should be implemented across multiple dependent groups
- The main agent needs to coordinate multiple subagents or implementation passes
- Group-to-group handover needs to be explicit and machine-readable

## Not Covered
- Creating the initial development plan
- Defining repository-wide guardrails or conventions
- Long-form human design discussion
- Final product acceptance sign-off by a human

## Source of Truth
- The structured development plan is the primary execution contract.
- Existing code remains the implementation source of truth for local patterns.
- Group handover artifacts are the source of truth for completed group state and downstream integration assumptions.
- If the plan conflicts with current code reality, the main agent must reconcile the mismatch explicitly instead of blindly executing the stale plan.

## Orchestration Lens
- Act as an orchestrator first, implementer second.
- Use the plan to determine execution order, scope boundaries, and handoff points.
- Keep each subagent focused on the smallest useful slice of work.
- Prefer explicit coordination over implicit assumptions between groups.
- Treat handovers as machine-readable delta artifacts, not narrative summaries.

## What to Read First
- The full development plan
- The current group's task slice
- Any prior handover artifacts from completed groups
- Only the code needed for the current group's assigned tasks
- The layer guide(s) for every layer the current group touches:
  - `.agents/guides/architecture.md`
  - `.agents/guides/layer-domain.md`
  - `.agents/guides/layer-application.md`
  - `.agents/guides/layer-infrastructure.md`
  - `.agents/guides/layer-presentation.md`
  - `.agents/guides/test-integration.md`
  - `.agents/guides/batch.md`

## What the Main Agent Must Do
- Read the plan and identify group order from dependencies and parallelization groups.
- Determine which tasks within a group can be worked independently.
- Dispatch subagents or implementation passes with a bounded scope per task or task cluster.
- Merge the returned results into a coherent group outcome.
- Validate whether the group output satisfies the assigned tasks before continuing.
- Write a machine-readable handover artifact after each completed group.
- Feed the next group only the plan, relevant prior handovers, and the group's task scope.
- After all groups finish, perform integration verification and prepare the final report for the human.

## Steering
- Do not send the entire repository context to every subagent by default.
- Do not collapse independent groups into one large implementation request unless the dependency graph requires it.
- If task independence is unclear, keep the tasks sequential.
- Keep handover artifacts factual, compact, and machine-readable.
- Record implementation deltas, not repository-wide rules, in handovers.
- Reconcile plan drift explicitly when real code constraints force a deviation.
- Keep task IDs, component IDs, and group IDs stable across orchestration artifacts.

## Completion Signals
- Every task in the plan is mapped to an execution group or explicitly deferred.
- Each completed group has a corresponding handover artifact.
- Later groups can be executed from the plan plus prior handovers without re-deriving upstream changes.
- The final report can explain what was implemented, what was deferred, and what was validated.

## Output Contract
The orchestration process must produce:
- One machine-readable handover artifact per completed group
- One final implementation report after all groups finish

## Templates
- Use the handover template at `references/group-handover-template.yaml`.
- Use the final report template at `references/final-implementation-report-template.md`.

## Handover Requirements
- Write one handover file per completed group.
- Each handover must capture only the delta introduced by that group.
- Each handover must include completed tasks, changed components, changed files, key decisions, exposed interfaces or contracts, validation performed, pending issues, and the next group's recommended focus.

## Final Report Requirements
- Summarize execution by group.
- Explain whether the implementation stayed within the original plan or drifted.
- Report actual validation performed.
- Surface unresolved risks, follow-up work, and any deferred tasks.
