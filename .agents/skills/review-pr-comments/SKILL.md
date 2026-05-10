# review-pr-comments

현재 브랜치의 PR에 달린 리뷰 코멘트를 가져와서 코드베이스와 대조하고, 사용자 지시에 따라 코드 수정 → 커밋 → 푸시 → GitHub 답변 게시까지 처리한다.

**사전 조건**: `gh` CLI가 설치되어 있고 `gh auth status`가 인증된 상태여야 한다.

---

## Phase 1: Fetch — PR 및 코멘트 조회

```bash
bash .agents/skills/review-pr-comments/scripts/fetch_pr_comments.sh
```

스크립트가 현재 브랜치를 감지해서 열린 PR을 찾고 리뷰 코멘트를 JSON으로 출력한다.

**파싱 규칙:**
- `in_reply_to_id`가 null이 아닌 코멘트는 제외한다 (이미 달린 답변 스레드, 내가 처리할 top-level 코멘트만 남긴다)
- 남은 top-level 코멘트 수를 사용자에게 알려준다

**오류 대응:**
- `열린 PR이 없습니다` → GitHub에서 PR이 open 상태인지 확인
- 인증 오류 → `gh auth status` 확인

---

## Phase 2: Analyze — 코드 분석 및 리뷰 요약 작성

top-level 코멘트 각각에 대해:

1. `comment.path` 파일을 열고 `comment.line` 기준 ±10줄을 읽는다
2. `comment.diff_hunk`를 보고 어떤 변경 맥락인지 파악한다
3. 리뷰어 의견이 타당한지, 어떻게 반영하는 게 좋을지 분석한다
4. 분석 내용을 **한국어로** 자연스럽게 작성한다 (동료한테 설명하듯이)

결과물 저장 경로:

```
.agents/output/pr-review-{branch-sanitized}.md
```

브랜치명의 `/`는 `-`로 치환한다. 예: `feature/TAS-495` → `pr-review-feature-TAS-495.md`

**Append 규칙 (같은 PR에 반복 호출 시):**
- 파일이 이미 있으면 새 코멘트만 추가한다 (기존 항목은 건드리지 않는다)
- 이미 있는 COMMENT-ID는 중복으로 추가하지 않는다
- 상단 "마지막 갱신" 날짜만 갱신한다

문서 형식은 `.agents/skills/review-pr-comments/references/review-summary-template.md`를 참고한다.

작성 완료 후 사용자에게 파일 위치를 알려주고 STATUS 결정을 요청한다.

---

## Phase 3: User Direction — 사용자 지시 수집

사용자가 STATUS를 채우면 (파일 수정 후 "업데이트했어" 또는 인라인으로 말해줘도 됨):

- 파일을 다시 읽거나 인라인 지시를 받는다
- 결정 요약 리포트: APPLY N건 / CUSTOM N건 / SKIP N건 / PENDING N건
- PENDING이 남아있으면 그냥 진행할지 물어본다
- 전부 결정되면 Phase 4로 넘어간다

---

## Phase 4: Apply → Push → Reply

### 4-1. 코드 수정

- **APPLY**: 리뷰어 제안 그대로 적용
- **CUSTOM**: 비고에 적힌 사용자 지시대로 적용
- 수정 범위는 해당 코멘트가 가리키는 코드에 한정한다. 주변 코드는 건드리지 않는다.

### 4-2. 커밋 및 푸시

변경 내용 확인:

```bash
git diff --stat
```

사용자 확인 후 커밋:

```bash
git add <변경된 파일들>
git commit -m "refactor: address PR review comments"
git push
```

푸시가 실패하면 (non-fast-forward) 오류만 리포트하고 멈춘다. force push 금지.

### 4-3. GitHub 답변 게시

OWNER와 REPO는 `gh repo view --json owner,name`으로 확인한다.

처리할 코멘트가 여러 개인 경우 순서대로 루프를 돌며 답변을 단다:

```bash
for COMMENT_ID in <id1> <id2> ...; do
  bash .agents/skills/review-pr-comments/scripts/post_reply.sh \
    --pr {pr.number} \
    --comment-id "$COMMENT_ID" \
    --body "{답변 내용}"
done
```

단건인 경우:

```bash
bash .agents/skills/review-pr-comments/scripts/post_reply.sh \
  --pr {pr.number} \
  --comment-id {comment.id} \
  --body "{답변 내용}"
```

답변 내용:
- **APPLY**: `"반영했습니다."`
- **CUSTOM**: `"반영했습니다. {실제로 적용한 내용 한 줄 요약}"`
- **SKIP**: `"확인했습니다. {스킵 이유}"`

SKIP도 답변을 단다. 리뷰어 입장에서 읽었다는 걸 알아야 하니까.

### 4-4. 요약 문서 업데이트

답변 게시가 완료된 코멘트의 STATUS를 `DONE`으로 바꾼다.

### 최종 리포트

- 수정된 파일 목록
- 커밋 해시
- 푸시 결과
- 답변 게시 성공/실패 (코멘트 ID별)

---

## 커맨드 레퍼런스

```bash
# PR + 코멘트 조회 (JSON 출력)
bash .agents/skills/review-pr-comments/scripts/fetch_pr_comments.sh

# 특정 코멘트에 답변 게시
bash .agents/skills/review-pr-comments/scripts/post_reply.sh \
  --pr <pr_number> \
  --comment-id <comment_id> \
  --body "<답변 내용>"
```
