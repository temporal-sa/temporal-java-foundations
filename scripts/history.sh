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

# `tdbg execution show` reads straight from the DB and requires an explicit run id (it does
# not resolve "latest" like the Web UI or the `temporal` CLI). Resolve it now; without one
# tdbg fails with a cryptic "Invalid RunId".
RID="$(current_run_id "$WID")"
if [ -z "$RID" ]; then
  echo "error: could not resolve a run id for workflow '$WID'." >&2
  echo "  'tdbg execution show' needs --run-id. Check the workflow exists (temporal workflow list)" >&2
  echo "  and that the 'temporal' CLI is installed so this script can look the run id up." >&2
  exit 1
fi

# NOTE: --address and -n/--namespace are GLOBAL tdbg flags — they must come BEFORE the
# `execution` subcommand, not after it.
CMD=("$TDBG" --address "$TEMPORAL_ADDRESS" -n "$TEMPORAL_NAMESPACE"
     execution show --workflow-id "$WID" --run-id "$RID" --decode)

if [ -n "$FILTER" ]; then
  "${CMD[@]}" | grep -iA4 "$FILTER"
else
  exec "${CMD[@]}"
fi
