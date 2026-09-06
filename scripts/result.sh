#!/usr/bin/env bash
# Print a workflow's status + result.
#   Usage:  ./scripts/result.sh [workflow-id]        (default: math-wf)
source "$(dirname "$0")/lib.sh"
need temporal

WID="${1:-$WORKFLOW_ID_DEFAULT}"
exec temporal workflow result --workflow-id "$WID"
