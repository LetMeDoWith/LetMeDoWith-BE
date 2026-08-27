# Guide: Integration Testing

## Policy

- Unit tests are intentionally not written.
- All tests are integration tests that run against a real test database.
- Test profile: `@ActiveProfiles("test")`, DB: `letmedowith_test`, DDL: `create-drop`

## AbstractIntegrationTest

Every test class must extend `AbstractIntegrationTest` and implement two abstract methods:

```java
private abstract void createTestData();   // called in @BeforeEach, after member setup

private abstract void deleteTestData();   // called in @AfterEach, before member clean
```

### What the base class sets up automatically

- `requestMember`: a `NORMAL` status member saved to DB
- `taskSummary`: linked `TaskSummary` for the test member (will be deprecated, so don't care about
  it)
- `requestMemberAccessToken` + `requestMemberRefreshToken`: valid tokens for the test member
- Clock reset: `SystemTimeUtil.resetClock()` called in `@AfterEach`

### Helper methods

| Method                                        | Purpose                                                          |
|-----------------------------------------------|------------------------------------------------------------------|
| `request(MockHttpServletRequestBuilder)`      | Performs HTTP call with auth headers (AUTHORIZATION, User-Agent) |
| `readResponse(ResultActions, Class<T>)`       | Deserializes `ResponseDto<T>.data`                               |
| `readPagingResponse(ResultActions, Class<T>)` | Deserializes `ResponsePageDto<T>.data`                           |
| `writeRequestBodyAsString(Object)`            | Serializes request body to JSON string                           |
| `setFixedClock(LocalDateTime)`                | Freezes `SystemTimeUtil` to a specific time                      |

### Time-dependent tests

Use `setFixedClock(LocalDateTime)` before the action under test.
Clock is automatically reset in `@AfterEach` — no manual reset needed.

```java
setFixedClock(LocalDateTime.of(2024, 3,1,10,0));
ResultActions result = request(get("/api/v1/tasks/1"));
```

## Test Structure Pattern

```java
class MyFeatureIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private XyzJpaRepository xyzJpaRepository;

    private Xyz testXyz;

    @Override
    protected void createTestData() {
        testXyz = xyzJpaRepository.save(Xyz.of(requestMember.getId(), ...))
    }

    @Override
    protected void deleteTestData() {
        xyzJpaRepository.deleteAll();
    }

    @Test
    void myTest() throws Exception {
        ResultActions result = request(get("/api/v1/xyz/{id}", testXyz.getId()));
        result.andExpect(status().isOk());

        XyzResDto body = readResponse(result, XyzResDto.class);
        assertThat(body.id()).isEqualTo(testXyz.getId());
    }
}
```

## Constraints

- Do not mock repositories or services — tests hit the real DB
- Do not call `SystemTimeUtil.resetClock()` manually — base class handles it
- Use `requestMember` (pre-created) as the authenticated actor; do not create additional member
  fixtures unless the test specifically requires a second user