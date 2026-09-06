#!/usr/bin/env bash
# ADVANCED / OPTIONAL. The Web UI (http://localhost:8080) is the primary way to read event
# history in these labs — this script is a terminal alternative for advanced users.
#
# Decode and print a workflow's event history straight from the database via tdbg. Handy in
# Lab 4/5: you can see the TimerStarted, the "Version" marker, and any WorkflowTaskFailed
# (non-determinism) events.
#
#   Usage:  ./scripts/history.sh [workflow-id] [grep-filter]
#   Examples:
#     ./scripts/history.sh                          # full decoded history of math-wf
#     ./scripts/history.sh math-wf TASK_FAILED      # just the non-determinism failures
#     ./scripts/history.sh math-wf MARKER_RECORDED  # the getVersion "Version" marker
#     ./scripts/history.sh math-wf TIMER_STARTED    # the await(timeout) park
source "$(dirname "$0")/lib.sh"
need_tdbg

WID="${1:-$WORKFLOW_ID_DEFAULT}"
FILTER="${2:-}"

# Closed executions need an explicit run id; resolve the latest one if we can.
RID="$(current_run_id "$WID")"
RID_ARGS=()
[ -n "$RID" ] && RID_ARGS=(--run-id "$RID")

CMD=("$TDBG" --address "$TEMPORAL_ADDRESS" -n "$TEMPORAL_NAMESPACE"
     execution show --workflow-id "$WID" "${RID_ARGS[@]}" --decode)

if [ -n "$FILTER" ]; then
  "${CMD[@]}" | grep -iA4 "$FILTER"
else
  exec "${CMD[@]}"
fi
