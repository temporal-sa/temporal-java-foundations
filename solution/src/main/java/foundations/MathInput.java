package foundations;

/**
 * Lab 1 input struct: the two operands the workflow starts with.
 *
 * Temporal serializes this to JSON as it crosses the client → server → worker
 * boundary, so it needs public fields (or getters) and a no-arg constructor.
 */
public class MathInput {
    public int a;
    public int b;

    public MathInput() {}

    public MathInput(int a, int b) {
        this.a = a;
        this.b = b;
    }
}
