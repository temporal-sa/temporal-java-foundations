#!/usr/bin/env bash
# Terminate the demo workflow so you can start fresh (e.g. between Lab 4 experiments).
#   Usage:  ./scripts/reset.sh [workflow-id]         (default: math-wf)
# To wipe ALL data (every workflow), instead run in your server checkout:
#   (cd "$TEMPORAL_SRC" && make install-schema-mysql)
source "$(dirname "$0")/lib.sh"
need temporal

WID="${1:-$WORKFLOW_ID_DEFAULT}"

temporal workflow terminate --workflow-id "$WID" --reason "foundations reset" 2>/dev/null \
  && echo "terminated $WID" \
  || echo "nothing to terminate for $WID (not running)"

echo "For a full data wipe: (cd \"$TEMPORAL_SRC\" && make install-schema-mysql)"
