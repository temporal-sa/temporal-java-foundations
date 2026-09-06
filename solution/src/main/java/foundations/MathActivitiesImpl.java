package foundations;

/**
 * Activity implementations. These run as ordinary Java — set a breakpoint in any
 * of them and it hits like normal code (no TEMPORAL_DEBUG needed for activities).
 */
public class MathActivitiesImpl implements MathActivities {

    @Override
    public int add(MathInput in) {
        // >>> BREAKPOINT (activity): inspect in.a and in.b. <<<
        return in.a + in.b;
    }

    @Override
    public int doubleValue(DoubleInput in) {
        // >>> BREAKPOINT (activity) <<<
        return in.value * 2;
    }

    @Override
    public int square(SquareInput in) {
        // >>> BREAKPOINT (activity) <<<
        return in.value * in.value;
    }
}
