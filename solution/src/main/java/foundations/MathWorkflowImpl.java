package foundations;

import io.temporal.activity.ActivityOptions;
import io.temporal.workflow.Workflow;
import java.time.Duration;

/**
 * The foundations workflow, fully assembled across Labs 1–4.
 *
 * Final formula:  (2 * (a + b) - submittedValue) ^ 2
 *
 *   Lab 1  sum     = add(a, b)                       a + b
 *   Lab 2  doubled = doubleValue(sum)               2 * (a + b)
 *   Lab 3  wait for submit(int), then               2 * (a + b) - submittedValue
 *          result  = doubled - submittedValue
 *   Lab 5  squared = square(result)                (2 * (a + b) - submittedValue) ^ 2
 *          — introduced safely with Workflow.getVersion.  (Lab 4 is read-only: it just
 *          reads the event history of what Labs 1–3 built.)
 *
 * Every activity call is a durable checkpoint: its result is written to history and
 * replayed (not re-run) if the worker restarts. That is why the workflow code must
 * stay deterministic — see Lab 5.
 */
public class MathWorkflowImpl implements MathWorkflow {

    private final MathActivities activities =
        Workflow.newActivityStub(
            MathActivities.class,
            ActivityOptions.newBuilder()
                // Generous timeout so a breakpoint held inside an activity never trips
                // the StartToClose timeout while you are debugging.
                .setStartToCloseTimeout(Duration.ofHours(1))
                .build());

    // ── Lab 3: signal state ──────────────────────────────────────────────────
    // A signal handler runs on the same thread as the workflow method, so these
    // plain fields need no locking.
    private boolean submitted;
    private int submittedValue;

    @Override
    public void submit(int value) {
        // >>> BREAKPOINT (signal handler): inspect `value` as it arrives. <<<
        this.submittedValue = value;
        this.submitted = true;
    }

    @Override
    public int run(MathInput in) {
        // >>> BREAKPOINT (workflow): step through the whole pipeline from here. <<<
        // Requires TEMPORAL_DEBUG=true on the worker (see .vscode/launch.json or .run/) so the
        // deadlock detector doesn't fire while you sit on a breakpoint.

        // ── Lab 1 ────────────────────────────────────────────────────────────
        int sum = activities.add(in);                                 // a + b

        // ── Lab 2 ────────────────────────────────────────────────────────────
        int doubled = activities.doubleValue(new DoubleInput(sum));   // 2 * (a + b)

        // ── Lab 3 ────────────────────────────────────────────────────────────
        // Park until submit(int) arrives. The timeout makes the park record a Timer
        // in history — that recorded command is what the Lab 5 Part A experiment collides
        // with, which is how a non-determinism error becomes observable.
        Workflow.await(Duration.ofHours(1), () -> submitted);
        int result = doubled - submittedValue;                        // 2*(a+b) - value

        // ── Lab 5 (determinism) ──────────────────────────────────────────────
        // `square` runs at the very END, so it only ever APPENDS history after the Timer
        // recorded by the await above — a safe append. (Contrast: the before-the-timer
        // insert in README Lab 5 Part A collides with that Timer and fails replay.) That
        // safe append is why even an already-parked execution runs square without error.
        // getVersion is the general safe-change tool for changes that AREN'T safe appends:
        // it returns DEFAULT_VERSION when replaying histories written before the change
        // (so they skip square and finish exactly as before), and version 1 for new
        // executions (which run square). One codebase serves old and new histories.
        int version = Workflow.getVersion("add-square-step", Workflow.DEFAULT_VERSION, 1);
        if (version == Workflow.DEFAULT_VERSION) {
            return result;                                            // pre-Lab-4 executions
        }
        int squared = activities.square(new SquareInput(result));     // ^2
        // >>> BREAKPOINT (workflow): inspect result and squared here. <<<
        return squared;
    }
}
