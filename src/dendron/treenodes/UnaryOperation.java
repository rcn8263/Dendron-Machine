package dendron.treenodes;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.Map;
import java.lang.Math.*;

/**
 * A subclass of ExpressionNode that represents a calculation of a
 * unary operator and an operand
 *
 * @author Ryan Nowak
 */
public class UnaryOperation implements ExpressionNode {
    static String NEG = "_";
    static Collection<String> OPERATORS;
    static String SQRT = "%";
    private String operator;
    private ExpressionNode expr;

    /**
     * Creates a new UnaryOperation node
     * Precondition: OPERATORS.contains( operator ), expr != null
     * @param operator A string that represents the operation
     * @param expr An ExpressionNode that represents the operand
     */
    public UnaryOperation(String operator, ExpressionNode expr) {
        this.operator = operator;
        this.expr = expr;
    }

    @Override
    public void infixDisplay() {
        System.out.print(operator);
        expr.infixDisplay();
    }

    @Override
    public void compile(PrintWriter out) {
        expr.compile(out);
        if (operator == NEG) {
            out.println("NEG ");
        }
        else if (operator == SQRT) {
            out.println("SQRT ");
        }
    }

    @Override
    public int evaluate(Map<String, Integer> symTab) {
        int result = 0;
        if (operator == NEG) {
            result = -expr.evaluate(symTab);
        }
        else if (operator == SQRT) {
            result = (int) Math.sqrt(expr.evaluate(symTab));
        }
        return result;
    }
}
