package dendron.treenodes;

import java.io.PrintWriter;
import java.util.Map;

/**
 * A subclass of ExpressionNode that represents a constant, literal value
 *
 * @author Ryan Nowak
 */
public class Constant implements ExpressionNode {
    private int value;

    /**
     * Creates a new Constant node that stores the given integer
     * @param value The integer that the node will contain
     */
    public Constant(int value) {
        this.value = value;
    }

    @Override
    public void infixDisplay() {
        System.out.print(value);
    }

    @Override
    public void compile(PrintWriter out) {
        out.println("PUSH " + this.value);
    }

    @Override
    public int evaluate(Map<String, Integer> symTab) {
        return this.value;
    }
}
