# Guide: Application Layer

## Role

Orchestration only. Services coordinate repositories, domain services, and entities.
Business logic belongs in the domain layer — not here.

## Service Naming

Verb + noun, scoped to a single use case:

- `CreateDowithTaskService`, `RetrieveRankingService`, `MemberService`
- A service class may have multiple methods if they share the same domain concern.

## Orchestration Pattern

Services follow this sequence:

1. Extract auth context: `AuthUtil.getMemberId()` — never receive memberId as a parameter from the
   caller
2. Validate preconditions (entity existence, access rights) — throw `RestApiException` on failure
3. Call domain factory or domain method
4. Call `@DomainService` if cross-entity logic is needed
5. Persist via repository
6. Return result

## @Transactional

- `@Transactional` boundaries belong at this layer.
- Use `@Transactional(readOnly = true)` on retrieval methods.
- Do not annotate domain or infrastructure classes with `@Transactional`.

## Command and Result DTOs

### Command (input)

- `record` with `@Builder`
- Primitive types or value objects only — no entities
- No validation logic inside the record

```java

@Builder
public record CreateDowithTaskCommand(String title, Long taskCategoryId, LocalDate date,
                                      LocalTime startTime) {

}
```

### Result (output)

- `record` without `@Builder` (immutable, use static factory)
- Static `from()` method transforms from QueryDto or domain object
- Nested records for complex structures

```java
public record RetrieveRankingsResult(List<RetrieveRankingResult> rankings) {

    public static RetrieveRankingsResult from(List<RankingsQueryDto> dtos) { ...}

    public record RetrieveRankingResult(Long round, Long topicId, ...) {

        public static RetrieveRankingResult from(RankingsQueryDto dto) { ...}
    }
}
```

## Return Types

- Command services may return the saved domain entity or `void`.
- Retrieval services return Result DTOs (not raw QueryDtos, not entities).
- Consistency of return types across all services is not fully enforced; follow the pattern of the
  adjacent service in the same domain.

## Javadoc

All public methods in application services must have a Javadoc comment.
Describe what the method does, not how — focus on the use case it fulfills.

## What Does NOT Belong Here

- No business rules or domain predicates — delegate to entity methods or `@DomainService`
- No QueryDSL or JPA imports
- No response formatting (ResDto, ResponseDto) — that belongs in the presentation layer