package foundations;

import io.temporal.client.WorkflowClient;
import io.temporal.serviceclient.WorkflowServiceStubs;

/**
 * Sends the submit(int) signal to a running MathWorkflow (Lab 3).
 * Args: [value] [workflowId].
 *
 * Equivalent Temporal CLI command:
 *   temporal workflow signal --workflow-id math-wf --name submit --input 5
 */
public class SignalSender {

    public static void main(String[] args) {
        int value = args.length > 0 ? Integer.parseInt(args[0]) : 5;
        String id = args.length > 1 ? args[1] : WorkerApp.WORKFLOW_ID;

        WorkflowServiceStubs service = WorkflowServiceStubs.newLocalServiceStubs();
        WorkflowClient client = WorkflowClient.newInstance(service);

        // Attach to the running execution by its Workflow ID, then signal it.
        MathWorkflow workflow = client.newWorkflowStub(MathWorkflow.class, id);
        workflow.submit(value);

        System.out.println("Sent submit(" + value + ") to WorkflowID=" + id);
        System.exit(0);
    }
}
