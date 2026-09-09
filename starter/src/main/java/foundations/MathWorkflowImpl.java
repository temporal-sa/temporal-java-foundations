package foundations;

import io.temporal.activity.ActivityOptions;
import io.temporal.workflow.Workflow;
import java.time.Duration;

/**
 * YOUR WORKING FILE. Build up (2 * (a + b) - submittedValue) ^ 2 across the labs.
 * Follow the numbered TODOs; the README walks through each one. A finished copy
 * lives in the `solution` module if you get stuck.
 *
 *   Lab 1  sum     = add(a, b)                       a + b
 *   Lab 2  doubled = doubleValue(sum)               2 * (a + b)
 *   Lab 3  wait for submit(int), then subtract      2 * (a + b) - submittedValue
 *   Lab 4  square the result (safely, via getVersion)  ... ^ 2
 */
public class MathWorkflowImpl implements MathWorkflow {

    // The activity stub is wired up for you. A generous StartToClose timeout means a
    // breakpoint held inside an activity won't trip the timeout while you debug.
    private final MathActivities activities =
        Workflow.newActivityStub(
            MathActivities.class,
            ActivityOptions.newBuilder()
                .setStartToCloseTimeout(Duration.ofHours(1))
                .build());

    // ── Lab 3: signal state ──────────────────────────────────────────────────
    // TODO Lab 3: add fields to remember whether submit() has arrived and its value,
    //             e.g.  private boolean submitted;  private int submittedValue;

    @Override
    public void submit(int value) {
        // TODO Lab 3: record `value` and flip your "submitted" flag to true.
    }

    @Override
    public int run(MathInput in) {
        // >>> BREAKPOINT (workflow): step through the pipeline from here. <<<
        // Requires TEMPORAL_DEBUG=true on the worker (see .vscode/launch.json or .run/).

        // ── Lab 1 ────────────────────────────────────────────────────────────
        // TODO Lab 1: call activities.add(in) and return the sum. Run it, confirm
        //             a=3 b=4 gives 7, then move on.

        // ── Lab 2 ────────────────────────────────────────────────────────────
        // TODO Lab 2: feed the sum into activities.doubleValue(new DoubleInput(sum))
        //             so the workflow now produces 2 * (a + b).

        // ── Lab 3 ────────────────────────────────────────────────────────────
        // TODO Lab 3: park until the signal arrives, then subtract:
        //   Workflow.await(Duration.ofHours(1), () -> submitted);
        //   int result = doubled - submittedValue;   // 2*(a+b) - value
        // The timeout on await() matters — see the README Lab 4 determinism note.

        // ── Lab 4 (determinism) ──────────────────────────────────────────────
        // TODO Lab 4: square the result, introduced safely with getVersion:
        //   int version = Workflow.getVersion("add-square-step",
        //                                      Workflow.DEFAULT_VERSION, 1);
        //   if (version == Workflow.DEFAULT_VERSION) return result;
        //   return activities.square(new SquareInput(result));

        throw new UnsupportedOperationException("TODO Lab 1: implement run()");
    }
}
