# Architecture Guide

## Layer Structure

Single Gradle module. Separation is by package convention only — not multi-module.

```
src/main/java/com/LetMeDoWith/LetMeDoWith/
├── domain/          # Entities, repository interfaces, @DomainService classes
├── application/     # Use-case services, Command/Result DTOs
├── presentation/    # @RestController, request/response DTOs
├── infrastructure/  # Repository impls, JPA repos, QueryDSL, external clients
├── common/          # Base entities, enums, exception handling, filters, utilities
├── batch/           # Spring Batch jobs and @Scheduled schedulers
└── config/          # Spring configuration
```

Domains: `auth`, `member`, `task`, `ranking`, `feedback`, `notification`, `notice`

Flow per domain: `domain/{name}` → `application/{name}` → `presentation/{name}` → `infrastructure/{name}`

## Repository Pattern

Domain defines the interface; infrastructure provides two distinct implementations:

- `infrastructure/{name}/persistence/XyzRepositoryImpl.java`  
  Write operations. Implements the domain repository interface. Annotated with `@Repository`.

- `infrastructure/{name}/query/XyzQueryRepositoryImpl.java`  
  Read-heavy queries using QueryDSL. **Not** part of the domain interface. Used directly by application services when complex filtering or projections are needed.

JPA interfaces live at `infrastructure/{name}/persistence/jpaRepository/`.

## Service Layer

Application services live in `application/{name}/service/`, named by action:  
e.g., `CreateTokenService`, `RetrieveRankingService`, `MemberService`

- `@Transactional` boundaries belong at this layer.
- Services operate on Command/Result/ValueObject DTOs, not raw JPA entities.
