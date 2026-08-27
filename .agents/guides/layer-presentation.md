# Guide: Presentation Layer

## Role

Receive HTTP requests, delegate to application services, wrap results in standard response format.
No business logic, no domain decisions.

## Response Format

### Standard response

```java
ResponseDto<T>(
    String statusCode, String
message,
T data)
```

### Paging response

```java
ResponsePageDto<T>(String statusCode, String message, long page, int size, int totalPage, long totalCount, T data)
```

Use `ResponseUtil.createSuccessResponse(dto)` to build the `ResponseEntity`. Never construct
`ResponseDto` manually in the controller.

### Choosing the format

- Single object or list without pagination metadata → `ResponseDto`
- Paginated list with page/size/total info → `ResponsePageDto`
- Read the existing API in the same domain to confirm which format applies before implementing.

## Controller Pattern

```java

@RestController
@RequestMapping("/api/v1/{resource}")
@RequiredArgsConstructor
@Tag(name = "...", description = "...")
public class XyzController {

    @Operation(summary = "...", description = "...")
    @ApiSuccessResponse(description = "...")
    @GetMapping("/{id}")
    public ResponseEntity<ResponseDto<XyzResDto>> getXyz(@PathVariable Long id) {
        return ResponseUtil.createSuccessResponse(
            XyzResDto.from(xyzService.retrieveXyz(id)));
    }
}
```

- `@ApiSuccessResponse` — required on every endpoint for Swagger
- `@Operation` — summary + description for each endpoint
- `@Tag` — on the class for grouping
- `XyzResDto.from(result)` — ResDto converts from application Result; never pass Result directly

## Exception Handling in Controllers

- Throw `RestApiException(FailResponseStatus.INVALID_PARAM_ERROR)` for invalid inputs caught at this
  layer (e.g., `size < 1`)
- Do not catch exceptions here — the global handler manages all conversion

## FailResponseStatus Constraint

- Use only existing values in `FailResponseStatus` — 34 defined codes covering client (E1XX-E2XX),
  auth (E3XX), and server errors (E4XX-E5XX)
- Do not add new enum values without deliberate discussion
- When in doubt, use `INVALID_REQUEST` for client errors and `SERVER_ERROR` for unexpected server
  failures

## ResDto (Response DTO)

- Location: `presentation/{name}/dto/response/`
- Static `from()` factory method converting from application Result DTO
- Records preferred; include only fields needed by the client
- Use canonical constructor or static factory only

## What Does NOT Belong Here

- No `@Transactional`
- No repository or domain service calls — only application service calls
- No business validation (that belongs in application or domain)