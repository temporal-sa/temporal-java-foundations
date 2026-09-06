#!/usr/bin/env bash
# Send the submit(int) signal to a running MathWorkflow (Lab 3+). This goes through the
# frontend, so it doesn't matter which module's worker is running.
#
#   Usage:  ./scripts/signal.sh [value] [workflow-id]     (default: value=5 id=math-wf)
source "$(dirname "$0")/lib.sh"
need temporal

VALUE="${1:-5}"; WID="${2:-$WORKFLOW_ID_DEFAULT}"

echo "signal 'submit' ($VALUE) -> $WID"
# An integer literal is already valid JSON, so --input takes it as-is.
exec temporal workflow signal --workflow-id "$WID" --name submit --input "$VALUE"
