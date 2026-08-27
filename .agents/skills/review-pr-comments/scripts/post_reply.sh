#!/usr/bin/env bash
set -euo pipefail

# 인자 파싱 — --pr <number> --comment-id <id> --body "<text>"
PR=""
COMMENT_ID=""
BODY=""

while [[ $# -gt 0 ]]; do
  case $1 in
    --pr)         PR="$2";         shift 2 ;;
    --comment-id) COMMENT_ID="$2"; shift 2 ;;
    --body)       BODY="$2";       shift 2 ;;
    *) echo "알 수 없는 인자: $1" >&2; exit 1 ;;
  esac
done

if [[ -z "$PR" || -z "$COMMENT_ID" || -z "$BODY" ]]; then
  echo '사용법: post_reply.sh --pr <number> --comment-id <id> --body "<text>"' >&2
  exit 1
fi

OWNER=$(gh repo view --json owner --jq '.owner.login')
REPO=$(gh repo view --json name --jq '.name')

gh api "repos/${OWNER}/${REPO}/pulls/${PR}/comments/${COMMENT_ID}/replies" \
  -f body="$BODY" \
  --jq '{success: true, comment_id: .id}'
