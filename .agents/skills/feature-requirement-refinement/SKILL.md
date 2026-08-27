---
name: feature-requirement-refinement
description: Use when a feature request is still rough and needs to be refined into a structured requirement artifact with reviewable Given/When/Then acceptance criteria before planning or coding.
metadata:
  short-description: Refine rough feature requests into structured acceptance criteria
---

# Feature Requirement Refinement

## Purpose
- Refine a loosely described feature request into a structured requirement artifact.
- Clarify scope, assumptions, and ambiguity before implementation planning begins.
- Produce human-reviewable acceptance criteria in Given / When / Then table form.

## Use When
- A feature request is still described in natural language
- The request is too vague to hand directly to a development planning skill
- The team needs a reviewable requirement artifact before planning or coding
- Scope, edge cases, or expected behavior need to be made explicit

## Not Covered
- Implementation planning
- Code design
- Code changes
- Test planning
- Verification of completed work

## Source of Truth
- The user's stated request is the primary source for product intent.
- Existing repository behavior, terminology, and constraints may be used as supporting context when relevant.
- Do not silently invent missing behavior; surface uncertainty as an assumption or open question.

## Refinement Lens
- Treat the request as a draft that needs shaping, not as a finished specification.
- Translate intent into observable behavior.
- Make scope explicit enough for human review.
- Produce output that can be handed to a planning skill without restating the feature.

## What to Clarify
- Feature goal
- Intended user or actor
- Included scope
- Excluded scope
- Success conditions
- Important edge cases
- Constraints or domain rules mentioned in the request
- Ambiguities that require human confirmation

## Steering
- Do not jump into implementation details or component-level design.
- Do not produce generic product-spec filler.
- Do not hide uncertainty behind confident language.
- Prefer concrete, observable behavior over vague summaries.
- Write acceptance criteria in Given / When / Then form.
- Present acceptance criteria in a table.

## Completion Signals
- The original request has been restated clearly.
- Scope and non-scope are explicit.
- Assumptions are separated from confirmed behavior.
- Acceptance criteria are concrete enough for human review.
- The output can be handed to a planning skill as input.

## Output Contract
The final response must include all sections below:
- `Feature Summary`
- `Scope`
- `Assumptions`
- `Acceptance Criteria`
- `Open Questions`

## Response Template
```md
## Feature Summary
- Restate the requested feature in 1-3 lines.
- Clarify the intended user goal and expected outcome.

## Scope
### In Scope
- Included behavior 1
- Included behavior 2

### Out of Scope
- Excluded behavior 1
- Excluded behavior 2

## Assumptions
- Assumption 1
- Assumption 2

## Acceptance Criteria

| ID | Given | When | Then |
|---|---|---|---|
| AC-1 | ... | ... | ... |
| AC-2 | ... | ... | ... |
| AC-3 | ... | ... | ... |

## Open Questions
- Question 1
- Question 2
```
