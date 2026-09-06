#!/usr/bin/env bash
# Start one MathWorkflow. From Lab 3 on it parks on the submit(int) signal, so this
# returns immediately — send the value next with signal.sh.
#
#   Usage:  ./scripts/start.sh [a] [b] [workflow-id]      (default: a=3 b=4 id=math-wf)
#           MODULE=solution ./scripts/start.sh 3 4
#
# The worker for the chosen MODULE must already be running (run-worker.sh or the IDE).
source "$(dirname "$0")/lib.sh"
check_module

A="${1:-3}"; B="${2:-4}"; WID="${3:-$WORKFLOW_ID_DEFAULT}"

GRADLE="$(gradle_cmd)" || {
  echo "No gradle wrapper found. Start from the IDE 'Start' config." >&2
  exit 1
}

echo "start MathWorkflow [$MODULE] a=$A b=$B -> $WID"
cd "$ROOT_DIR"
exec "$GRADLE" -q ":$MODULE:runStarter" --args="$A $B $WID"
