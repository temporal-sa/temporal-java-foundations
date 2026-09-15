#!/usr/bin/env bash
# ADVANCED / OPTIONAL. The Web UI (http://localhost:8080) shows this too — this is the
# terminal alternative for advanced users.
#
# Show a workflow's decoded mutable state (tdbg) + high-level status (temporal CLI).
#   Usage:  ./scripts/describe.sh [workflow-id]      (default: math-wf)
source "$(dirname "$0")/lib.sh"

WID="${1:-$WORKFLOW_ID_DEFAULT}"

if [ -x "$TDBG" ]; then
  echo "== tdbg execution describe (decoded mutable state) =="
  # --address and -n/--namespace are GLOBAL tdbg flags — they go BEFORE the `execution`
  # subcommand. Unlike `execution show`, `describe` defaults to the latest run, so no run id.
  "$TDBG" --address "$TEMPORAL_ADDRESS" -n "$TEMPORAL_NAMESPACE" \
    execution describe --workflow-id "$WID" || true
  echo
fi

if command -v temporal >/dev/null 2>&1; then
  echo "== temporal workflow describe (status) =="
  temporal workflow describe --workflow-id "$WID" || true
fi
