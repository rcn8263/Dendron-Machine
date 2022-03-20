package dendron.treenodes;

import dendron.Errors;

import java.io.PrintWriter;
import java.util.Map;

/**
 * A subclass of ExpressionNode that represents a simple variable
 *
 * @author Ryan Nowak
 */
public class Variable implements ExpressionNode {
    private String name;

    /**
     * Creates a Variable node with the given name
     * @param name A string that represents the name of the new variable
     */
    public Variable(String name) {
        this.name = name;
    }

    @Override
    public void infixDisplay() {
        System.out.print(this.name);
    }

    @Override
    public void compile(PrintWriter out) {
        out.println("LOAD " + name);
    }

    @Override
    public int evaluate(Map<String, Integer> symTab) {
        if (symTab.get(this.name) == null) {
            Errors.report(Errors.Type.UNINITIALIZED, this.name);
        }
        return symTab.get(this.name);
    }
}
