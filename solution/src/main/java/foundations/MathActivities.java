package foundations;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

/**
 * The activities the workflow orchestrates. Each activity is a normal method that
 * runs on the worker; Temporal records its result in history as a durable
 * checkpoint, so it never re-executes on replay.
 */
@ActivityInterface
public interface MathActivities {

    /** Lab 1: a + b */
    @ActivityMethod
    int add(MathInput in);

    // Named doubleValue because `double` is a Java keyword.
    /** Lab 2: value * 2 */
    @ActivityMethod
    int doubleValue(DoubleInput in);

    /** Lab 4: value * value */
    @ActivityMethod
    int square(SquareInput in);
}
