package foundations;

import io.temporal.workflow.SignalMethod;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

/**
 * The foundations workflow. One @WorkflowMethod is the entry point; the
 * @SignalMethod delivers an asynchronous value into a running execution (Lab 3).
 */
@WorkflowInterface
public interface MathWorkflow {

    /** Entry point. Returns (2 * (a + b) - submittedValue) ^ 2 once complete. */
    @WorkflowMethod
    int run(MathInput in);

    /** Lab 3: deliver the value to subtract into an already-running execution. */
    @SignalMethod
    void submit(int value);
}
