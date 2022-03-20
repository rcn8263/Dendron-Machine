package dendron.treenodes;

import java.io.PrintWriter;
import java.util.Map;

/**
 * A subclass of ActionNode that displays the value of an expression
 * on the console
 *
 * @author Ryan Nowak
 */
public class Print implements ActionNode {
    static String PRINT_PREFIX = "=== ";
    private ExpressionNode printee;

    /**
     * Creates a Print node with the given expression
     * @param printee The expression that is evaluated and printed
     */
    public Print(ExpressionNode printee) {
        this.printee = printee;
    }

    @Override
    public void execute(Map<String, Integer> symTab) {
        int result = printee.evaluate(symTab);
        System.out.println(PRINT_PREFIX + result);
    }

    @Override
    public void infixDisplay() {
        System.out.print("Print ");
        this.printee.infixDisplay();
    }

    @Override
    public void compile(PrintWriter out) {
        printee.compile(out);
        out.println("PRINT");
    }
}
