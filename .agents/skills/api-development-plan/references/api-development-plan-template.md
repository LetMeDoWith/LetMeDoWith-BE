# API Development Plan Template

## Requirement Summary
- Restate the requested API briefly and concretely.
- Clarify the intended behavior and planning scope.

## References
- Existing implementations reviewed:
- Why they are relevant:
- Patterns worth reusing:

## Design Decisions
- Recommended design direction:
- Why this direction fits the codebase:
- Key tradeoffs or alternatives considered:
- Whether this follows an established precedent or creates a minimal new precedent:

## Component Inventory
| Component ID | Component Type | Create or Modify | Responsibility | Likely Location |
|---|---|---|---|---|
| CMP-1 | Controller | Create / Modify | ... | ... |
| CMP-2 | Request DTO | Create / Modify | ... | ... |
| CMP-3 | Response DTO | Create / Modify | ... | ... |
| CMP-4 | Application Service | Create / Modify | ... | ... |
| CMP-5 | Domain Entity / Domain Service | Create / Modify | ... | ... |
| CMP-6 | Repository / Persistence | Create / Modify | ... | ... |
| CMP-7 | Additional Component | Create / Modify | ... | ... |

## Development Task Plan
| Task ID | Task | Goal | Depends On | Target Components |
|---|---|---|---|---|
| TASK-1 | ... | ... | None | CMP-1, CMP-2 |
| TASK-2 | ... | ... | TASK-1 | CMP-4, CMP-5 |
| TASK-3 | ... | ... | TASK-2 | CMP-6 |

## Parallelization Groups
| Group ID | Tasks | Parallelizable | Reason |
|---|---|---|---|
| GRP-1 | TASK-1, TASK-2 | No | Shared design dependency |
| GRP-2 | TASK-3, TASK-4 | Yes | Independent target components after shared contracts are fixed |

## Recommended Flow
- Request entry:
- Main business flow:
- Data or dependency touchpoints:
- Response construction:

## Constraints
- Existing patterns to preserve:
- Whether precedent is strong or weak in this area:
- Structures to avoid introducing:
- Requirement or codebase constraints:

## Open Questions
- Assumptions made:
- Unclear requirement points:
- Follow-up decisions needed before implementation:
