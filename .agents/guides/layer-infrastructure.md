# Guide: Infrastructure Layer

## CQRS Split

Infrastructure repositories split by purpose, not by entity:

| Type        | Location                                                   | Purpose                                                   | Implements                  |
|-------------|------------------------------------------------------------|-----------------------------------------------------------|-----------------------------|
| Persistence | `infrastructure/{name}/persistence/XyzRepositoryImpl.java` | CUD + reads that support CUD flows                        | Domain repository interface |
| Query       | `infrastructure/{name}/query/XyzQueryRepositoryImpl.java`  | Standalone read queries (listing, filtering, projections) | Separate query interface    |

A `findById` needed to validate existence before update → **persistence**.
A `findAll` with filters for a list screen → **query**.

## Persistence Repository

- Annotated with `@Repository`, `@RequiredArgsConstructor`
- Implements the domain repository interface
- Delegates to one or more `XyzJpaRepository` interfaces (Spring Data JPA)
- JPA repository interfaces live at: `persistence/jpaRepository/`

```java

@Repository
@RequiredArgsConstructor
public class RankingRepositoryImpl implements RankingRepository {

    private final RankingTopicJpaRepository rankingTopicJpaRepository;

    @Override
    public Optional<RankingTopic> getRankingTopic(RankingTopicCode code, Yn isActive) {
        return rankingTopicJpaRepository.findByCodeAndIsActive(code, isActive);
    }
}
```

## Query Repository

- Annotated with `@Repository`, `@RequiredArgsConstructor`
- Uses `JPAQueryFactory` (QueryDSL)
- Q-types declared as instance fields
- Results mapped via `Projections.constructor(XyzQueryDto.class, ...)`
- Returns QueryDtos — never domain entities

```java

@Repository
@RequiredArgsConstructor
public class RankingQueryRepositoryImpl implements RankingQueryRepository {

    private final JPAQueryFactory queryFactory;
    private final QRankingTopic qRankingTopic = QRankingTopic.rankingTopic;

    @Override
    public List<RankingTopicsQueryDto> getRankingTopics() {
        return queryFactory
            .select(Projections.constructor(RankingTopicsQueryDto.class, ...))
            .from(qRankingTopic)
            .where(...)
            .fetch();
    }
}
```

## QueryDto

- Location: `infrastructure/{name}/query/dto/` [INFERRED — verify for new domains]
- Plain records or classes matching the projection columns
- Not exposed above the application layer; application services map these to Result DTOs

## What Does NOT Belong Here

- No domain rules or business logic in repositories
- No `@Transactional` — managed at the application layer
- Persistence repos must not be called directly from controllers