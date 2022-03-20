package dendron.treenodes;

import dendron.Errors;

import java.io.PrintWriter;
import java.util.Map;

/**
 * A subclass of ActionNode that represents the assignment of the value of
 * an expression to a variable

 * @author Ryan Nowak
 */
public class Assignment implements ActionNode {
    private String ident;
    private ExpressionNode rhs;

    /**
     * Creates an Assignment node that represents the assignment of a
     * value of an expression to a variable.
     * @param ident The name of the variable that is assigned a value
     * @param rhs The expression that is being evaluated
     */
    public Assignment(String ident, ExpressionNode rhs) {
        if (ident.matches("-?\\d+")) {
            Errors.report(Errors.Type.ILLEGAL_VALUE, ident);
        }
        this.ident = ident;
        this.rhs = rhs;
    }

    @Override
    public void execute(Map<String, Integer> symTab) {
        int result = rhs.evaluate(symTab);
        symTab.put(this.ident, result);
    }

    @Override
    public void infixDisplay() {
        System.out.print(ident + " := ");
        rhs.infixDisplay();
    }

    @Override
    public void compile(PrintWriter out) {
        rhs.compile(out);
        out.println("STORE " + this.ident);
    }
}
