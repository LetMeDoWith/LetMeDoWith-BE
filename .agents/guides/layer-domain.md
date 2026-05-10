# Guide: Domain Layer

## Role
- Entities hold business logic and state transitions.
- Domain services handle logic that spans multiple entities or requires external computation.
- Repository interfaces define the contract; implementation lives in infrastructure.

## Entity Rules

### Construction
- Constructor access: `PROTECTED` (enforced via `@Builder(access = AccessLevel.PROTECTED)`)
- Use static factory methods (`Entity.of(...)`) for instantiation — not constructors directly.
- Validation is triggered inside factory methods or domain methods, not in services.

```java
// Correct
DowithTask task = DowithTask.of(memberId, categoryId, title, date, startTime);

// Never do this
new DowithTask(...);
```

### Business Logic
- State predicates belong on the entity: `isStarted()`, `isFeedbackAvailable()`, `isRoutine()`
- State transitions are explicit domain methods: `success()`, `withdraw()`, `updatePersonalInfo()`
- Methods that mutate state return `this` for chaining or `void`.
- Methods that produce a new value object return it directly.

### Aggregate Roots
- Aggregate roots are annotated with `@AggregateRoot`.
- Child relationships (e.g., successes, likes) are managed through the root entity only.

### Association Rules
- **Same aggregate**: reference the entity object directly via JPA (`@ManyToOne`, `@OneToMany`, etc.)
- **Different domain**: reference by id only (e.g., `String memberId`, `Long taskId`) — no JPA join across domain boundaries

```java
// Within the same aggregate — object reference
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "routine_id")
private DowithTaskRoutine routine;

// Cross-domain — id reference only
private String memberId;   // references Member domain, no @ManyToOne
```

## @DomainService Rules
- Use when logic cannot fit in a single entity: spans multiple entities, or requires a strategy/algorithm.
- Examples: `TaskRoutineDateCalculator` (routine date computation), `TodoTaskRoutineSplitter` (splitting a routine at a pivot)
- Do not inject repositories into `@DomainService`. Pass required data as parameters.
- `@DomainService` is a Spring `@Component` — it can be injected into application services.
- All public methods must have a Javadoc comment.

## Repository Interface
- Location: `domain/{name}/repository/XyzRepository.java`
- Define only the operations the domain needs. Do not leak JPA or QueryDSL into the interface.
- Implementation is in infrastructure — domain layer has no knowledge of it.

## What Does NOT Belong Here
- No `@Transactional` — transaction boundaries are at the application layer.
- No orchestration logic — that belongs in application services.
- No infrastructure dependencies (no JPA, no QueryDSL, no Spring Data).
