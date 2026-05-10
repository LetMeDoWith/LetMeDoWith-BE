#!/usr/bin/env bash

set -euo pipefail

REMOTE="origin"
BASE_INPUT=""
NO_FETCH=0

usage() {
  cat <<'EOF'
Usage: collect_pr_context.sh [--base <branch>] [--remote <name>] [--no-fetch]

Collect branch comparison context for PR drafting.
EOF
}

while [[ $# -gt 0 ]]; do
  case "$1" in
    --base)
      BASE_INPUT="${2:-}"
      shift 2
      ;;
    --remote)
      REMOTE="${2:-}"
      shift 2
      ;;
    --no-fetch)
      NO_FETCH=1
      shift
      ;;
    -h|--help)
      usage
      exit 0
      ;;
    *)
      echo "Unknown argument: $1" >&2
      usage >&2
      exit 1
      ;;
  esac
done

if ! git rev-parse --git-dir >/dev/null 2>&1; then
  echo "Not inside a git repository." >&2
  exit 1
fi

resolve_base_ref() {
  local candidate="$1"
  if [[ -z "$candidate" ]]; then
    return 1
  fi

  if git rev-parse --verify --quiet "$candidate" >/dev/null; then
    printf '%s\n' "$candidate"
    return 0
  fi

  if git rev-parse --verify --quiet "refs/remotes/${REMOTE}/${candidate}" >/dev/null; then
    printf '%s\n' "${REMOTE}/${candidate}"
    return 0
  fi

  if git rev-parse --verify --quiet "refs/heads/${candidate}" >/dev/null; then
    printf '%s\n' "$candidate"
    return 0
  fi

  return 1
}

detect_base_ref() {
  local remote_head=""
  local candidate=""

  if [[ -n "$BASE_INPUT" ]]; then
    resolve_base_ref "$BASE_INPUT" && return 0
    echo "Could not resolve base branch: $BASE_INPUT" >&2
    exit 1
  fi

  if git rev-parse --verify --quiet "refs/remotes/${REMOTE}/develop" >/dev/null; then
    printf '%s\n' "${REMOTE}/develop"
    return 0
  fi

  remote_head="$(git symbolic-ref --quiet --short "refs/remotes/${REMOTE}/HEAD" 2>/dev/null || true)"
  remote_head="${remote_head#${REMOTE}/}"
  if [[ -n "$remote_head" ]]; then
    resolve_base_ref "${REMOTE}/${remote_head}" && return 0
  fi

  for candidate in main master; do
    resolve_base_ref "${REMOTE}/${candidate}" && return 0
  done

  for candidate in develop main master; do
    resolve_base_ref "$candidate" && return 0
  done

  echo "Could not determine a base branch." >&2
  exit 1
}

fetch_status="skipped (--no-fetch)"
if [[ "$NO_FETCH" -eq 0 ]]; then
  if git fetch --all --prune >/dev/null 2>&1; then
    fetch_status="ok"
  else
    fetch_status="failed (using local refs)"
  fi
fi

current_branch="$(git rev-parse --abbrev-ref HEAD)"
base_ref="$(detect_base_ref)"
merge_base="$(git merge-base "$base_ref" HEAD)"
commit_count="$(git rev-list --count "${merge_base}..HEAD")"
working_tree_status="$(git status --short)"
diff_stat="$(git diff --shortstat "$merge_base" HEAD || true)"

echo "== PR Context =="
echo "current_branch: ${current_branch}"
echo "base_ref: ${base_ref}"
echo "merge_base: ${merge_base}"
echo "fetch_status: ${fetch_status}"
if [[ -z "$working_tree_status" ]]; then
  echo "working_tree: clean"
else
  echo "working_tree: dirty"
fi

echo
echo "== Commit Summary =="
echo "commit_count: ${commit_count}"
if [[ "$commit_count" -eq 0 ]]; then
  echo "(no committed changes against base)"
else
  git log --reverse --pretty=format:'- %h %s' "${merge_base}..HEAD"
  echo
fi

echo
echo "== Changed Files =="
if git diff --quiet "$merge_base" HEAD; then
  echo "(no committed file changes against base)"
else
  git diff --name-status "$merge_base" HEAD
fi

echo
echo "== Diff Stat =="
if [[ -n "$diff_stat" ]]; then
  echo "$diff_stat"
else
  echo "(no diff stat available)"
fi

echo
echo "== Directory Distribution =="
if git diff --quiet "$merge_base" HEAD; then
  echo "(no committed directory changes against base)"
else
  git diff --dirstat=files,0 "$merge_base" HEAD
fi

echo
echo "== Working Tree Status =="
if [[ -z "$working_tree_status" ]]; then
  echo "(clean)"
else
  printf '%s\n' "$working_tree_status"
fi
