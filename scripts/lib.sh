#!/usr/bin/env bash
# Shared config + helpers for the foundations lab scripts. Source this; don't run it.
set -euo pipefail

# Connection — the `temporal` CLI honors these env vars.
export TEMPORAL_ADDRESS="${TEMPORAL_ADDRESS:-localhost:7233}"
export TEMPORAL_NAMESPACE="${TEMPORAL_NAMESPACE:-default}"

# Which module the worker/starter run from: starter (your code) or solution (reference).
#   MODULE=solution ./scripts/run-worker.sh
MODULE="${MODULE:-starter}"

WORKFLOW_ID_DEFAULT="math-wf"

# Paths — this file lives in <repo>/scripts/. ROOT_DIR is the repo root.
SCRIPTS_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(cd "$SCRIPTS_DIR/.." && pwd)"

# The debug Temporal server + its tooling (tdbg, the MySQL dev stack) live in a SEPARATE
# checkout of temporalio/temporal that you built in PREREQUISITES.md. Only the ADVANCED
# scripts (history.sh, describe.sh, sql.sh) use these. Point TEMPORAL_SRC at that checkout
# if the default is wrong.
TEMPORAL_SRC="${TEMPORAL_SRC:-$HOME/temporal-oss/temporal}"
TDBG="${TDBG:-$TEMPORAL_SRC/tdbg}"
MYSQL_CONTAINER="${MYSQL_CONTAINER:-temporal-dev-mysql}"

need() {
  command -v "$1" >/dev/null 2>&1 || { echo "error: '$1' not found in PATH" >&2; exit 1; }
}

need_tdbg() {
  [ -x "$TDBG" ] || {
    echo "error: tdbg not found at $TDBG" >&2
    echo "  tdbg is optional (advanced). Build it in your Temporal server checkout:" >&2
    echo "    (cd \"\$TEMPORAL_SRC\" && make tdbg)" >&2
    echo "  If your checkout is elsewhere:  export TEMPORAL_SRC=/path/to/temporal" >&2
    exit 1
  }
}

check_module() {
  case "$MODULE" in
    starter|solution) ;;
    *) echo "error: MODULE must be 'starter' or 'solution' (got '$MODULE')" >&2; exit 1 ;;
  esac
}

# Echo the gradle command to use: the project wrapper if present, else a system gradle.
gradle_cmd() {
  if [ -x "$ROOT_DIR/gradlew" ]; then
    echo "$ROOT_DIR/gradlew"
  elif command -v gradle >/dev/null 2>&1; then
    echo "gradle"
  else
    return 1
  fi
}

# Resolve the current run id of a workflow (needs the `temporal` CLI). Empty if unknown.
current_run_id() {
  local wid="$1"
  command -v temporal >/dev/null 2>&1 || return 0
  temporal workflow describe --workflow-id "$wid" -o json 2>/dev/null \
    | sed -n 's/.*"runId": *"\([0-9a-fA-F-]*\)".*/\1/p' | head -1
}
