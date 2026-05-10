# PR 리뷰 요약 — {branch}

**PR**: #{number} — {title}
**URL**: {html_url}
**마지막 갱신**: {date}

---

## 사용법

STATUS 컬럼에 아래 중 하나를 채우면 된다:

| 값 | 의미 |
|----|------|
| `APPLY` | 리뷰어 제안 그대로 적용 |
| `SKIP` | 적용 안 함 — 왜 스킵인지 비고에 적어줘 |
| `CUSTOM` | 방향은 다르게 — 비고에 원하는 내용 적어줘 |
| `PENDING` | 아직 결정 안 함 (기본값) |

---

## 코멘트 목록

| # | 파일 | 라인 | 작성자 | STATUS | 비고 |
|---|------|------|--------|--------|------|
| {n} | `{path}` | {line} | @{user} | PENDING | |

---

## 코멘트 상세

### [{n}] COMMENT-ID: {id}

**파일**: `{path}` (line {line})
**작성자**: @{user} · {created_at}

**Diff:**
```diff
{diff_hunk}
```

**리뷰어 코멘트:**
> {body}

**관련 코드 ({start}–{end}줄):**
```java
{code_excerpt}
```

**내 생각:**
{claude_analysis}

**STATUS**: PENDING
**비고**:

---
