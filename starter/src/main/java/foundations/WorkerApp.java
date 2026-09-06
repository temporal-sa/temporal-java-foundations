package foundations;

import io.temporal.client.WorkflowClient;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;

/**
 * Hosts the workflow + activity code. Start this first (or run "Worker (debug)"
 * from the Run and Debug panel), then kick off an execution with {@link Starter}.
 */
public class WorkerApp {

    // Unique per module so the starter and solution workers don't cross-poll.
    static final String TASK_QUEUE = "foundations-java";
    static final String WORKFLOW_ID = "math-wf";

    public static void main(String[] args) {
        // Connects to localhost:7233, namespace "default" (the `temporal server
        // start-dev` defaults).
        WorkflowServiceStubs service = WorkflowServiceStubs.newLocalServiceStubs();
        WorkflowClient client = WorkflowClient.newInstance(service);
        WorkerFactory factory = WorkerFactory.newInstance(client);

        Worker worker = factory.newWorker(TASK_QUEUE);
        worker.registerWorkflowImplementationTypes(MathWorkflowImpl.class);
        worker.registerActivitiesImplementations(new MathActivitiesImpl());

        System.out.println("Worker started on task queue: " + TASK_QUEUE);
        factory.start();
    }
}
