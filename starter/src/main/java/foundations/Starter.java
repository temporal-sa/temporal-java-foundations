package foundations;

import io.temporal.api.common.v1.WorkflowExecution;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import java.time.Duration;

/**
 * Starts one MathWorkflow and returns immediately (it does NOT wait for the result,
 * because Lab 3 parks the workflow on a signal). Args: [a] [b] [workflowId].
 */
public class Starter {

    public static void main(String[] args) {
        int a = args.length > 0 ? Integer.parseInt(args[0]) : 3;
        int b = args.length > 1 ? Integer.parseInt(args[1]) : 4;
        String id = args.length > 2 ? args[2] : WorkerApp.WORKFLOW_ID;

        WorkflowServiceStubs service = WorkflowServiceStubs.newLocalServiceStubs();
        WorkflowClient client = WorkflowClient.newInstance(service);

        WorkflowOptions options =
            WorkflowOptions.newBuilder()
                .setWorkflowId(id)
                .setTaskQueue(WorkerApp.TASK_QUEUE)
                // Long workflow-task timeout gives you headroom to sit on a breakpoint
                // in WORKFLOW code without the task timing out.
                .setWorkflowTaskTimeout(Duration.ofMinutes(15))
                .build();

        MathWorkflow workflow = client.newWorkflowStub(MathWorkflow.class, options);
        WorkflowExecution execution = WorkflowClient.start(workflow::run, new MathInput(a, b));

        System.out.println(
            "Started MathWorkflow WorkflowID=" + id
                + " RunID=" + execution.getRunId()
                + "  (a=" + a + ", b=" + b + ")");
        System.out.println("Next: send the submit signal, e.g.  ./gradlew :starter:runSignal --args=\"5\"");
        System.exit(0);
    }
}
