#!/usr/bin/env bash
set -euo pipefail

# 현재 브랜치 확인
BRANCH=$(git rev-parse --abbrev-ref HEAD 2>/dev/null) || {
  echo '[review-pr-comments] git 브랜치를 읽을 수 없습니다.' >&2
  exit 1
}

# PR 조회
PR_JSON=$(gh pr view --json number,title,url,baseRefName,headRefName 2>/dev/null) || {
  echo "[review-pr-comments] '${BRANCH}' 브랜치에 열린 PR이 없습니다." >&2
  exit 1
}

PR_NUMBER=$(echo "$PR_JSON" | jq -r '.number')
OWNER=$(gh repo view --json owner --jq '.owner.login')
REPO=$(gh repo view --json name --jq '.name')

# 리뷰 코멘트 조회
COMMENTS=$(gh api "repos/${OWNER}/${REPO}/pulls/${PR_NUMBER}/comments" \
  --paginate \
  --jq '[.[] | {id, path, position, line, diff_hunk, body, user: .user.login, created_at, in_reply_to_id}]')

jq -n \
  --argjson pr "$PR_JSON" \
  --argjson comments "$COMMENTS" \
  '{
    pr: {
      number:      $pr.number,
      title:       $pr.title,
      html_url:    $pr.url,
      base_branch: $pr.baseRefName,
      head_branch: $pr.headRefName
    },
    comments: $comments
  }'
