#!/usr/bin/env bash
# Run the worker from the terminal — handy for the Lab 4 determinism demo, where you
# repeatedly kill + restart the worker. For breakpoint debugging, use the IDE
# "Worker (debug)" launch config instead.
#
#   Usage:  ./scripts/run-worker.sh
#           MODULE=solution ./scripts/run-worker.sh
#
# Sets TEMPORAL_DEBUG=true so breakpoints in workflow code don't trip the deadlock detector.
source "$(dirname "$0")/lib.sh"
check_module

GRADLE="$(gradle_cmd)" || {
  echo "No gradle wrapper found. Run the worker from the IDE 'Worker (debug)' config." >&2
  exit 1
}

echo "Java worker [$MODULE] on task queue foundations-java (Ctrl-C to stop)"
cd "$ROOT_DIR"
TEMPORAL_DEBUG=true exec "$GRADLE" -q ":$MODULE:run"
