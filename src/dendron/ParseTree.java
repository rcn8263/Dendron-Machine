package dendron;

import dendron.treenodes.*;

import javax.swing.*;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Operations that are done on a Dendron code parse tree.
 *
 * @author Ryan Nowak
 */
public class ParseTree {

    static String ASSIGN = ":=";
    static String PRINT = "#";
    private Program program = new Program();
    private List<String> tokens;

    /**
     * Parse the entire list of program tokens. The program is a
     * sequence of actions (statements), each of which modifies something
     * in the program's set of variables. The resulting parse tree is
     * stored internally.
     * @param tokens the token list (Strings). This list may be destroyed
     *                by this constructor.
     */
    public ParseTree( List< String > tokens ) {
        this.tokens = tokens;
        while (!this.tokens.isEmpty()) {
            this.program.addAction(parseAction());
        }
    }

    /**
     * Parses the next token and creates an ActionNode for that token.
     * Then calls parseExpression to parse the remaining tokens for the action.
     * @return ActionNode if next token in tokens is a valid action.
     */
    private ActionNode parseAction() {
        if (this.tokens.isEmpty()) {
            Errors.report(Errors.Type.PREMATURE_END, null);
        }
        if (this.tokens.get(0).equals(ASSIGN)) {
            this.tokens.remove(0);
            if (this.tokens.isEmpty()) {
                Errors.report(Errors.Type.PREMATURE_END, null);
            }
            String ident = this.tokens.remove(0);
            return new Assignment(ident, parseExpression());
        }
        else if (this.tokens.get(0).equals(PRINT)) {
            this.tokens.remove(0);
            if (this.tokens.isEmpty()) {
                Errors.report(Errors.Type.PREMATURE_END, null);
            }
            return new Print(parseExpression());
        }
        else {
            Errors.report(Errors.Type.ILLEGAL_VALUE, this.tokens.get(0));
        }
        return null;
    }

    /**
     * Parses the remaining tokens that are part of the same action.
     * Creates ExpressionNodes for each token.
     * @return ExpressionNode that contains the entire expression
     */
    private ExpressionNode parseExpression() {
        ExpressionNode result = null;
        if (this.tokens.isEmpty()) {
            Errors.report(Errors.Type.ILLEGAL_VALUE, this.tokens);
        }
        else {
            String token = this.tokens.remove(0);
            if (token.matches("-?\\d+")) {
                result = new Constant(Integer.parseInt(token));
            }
            else if (token.matches("^[a-zA-Z].*")) {
                result = new Variable(token);
            }
            else if (token.equals("_") || token.equals("%")) {
                result = new UnaryOperation(token, parseExpression());
            }
            else if (token.equals("+") || token.equals("-") ||
                     token.equals("*") || token.equals("/")) {
                result = new BinaryOperation(token, parseExpression(),
                                                    parseExpression());
            }
            else {
                Errors.report(Errors.Type.ILLEGAL_VALUE, token);
            }
        }
        return result;
    }

    /**
     * Print the program the tree represents in a more typical
     * infix style, and with one statement per line.
     * @see ActionNode#infixDisplay()
     */
    public void displayProgram() {
        System.out.println("The Program, with expressions in infix notation:\n");
        this.program.infixDisplay();
        System.out.println();
    }

    /**
     * Run the program represented by the tree directly
     * @see ActionNode#execute(Map)
     */
    public void interpret() {
        System.out.println("Interpreting the parse tree...");
        Map <String, Integer> symTab = new HashMap<String, Integer>();
        this.program.execute(symTab);
        System.out.println("Interpretation complete.\n");

        System.out.println("Symbol Table Contents");
        System.out.println("========================");
        for (String key: symTab.keySet()) {
            System.out.println(key + " :   " + symTab.get(key));
        }
    }

    /**
     * Build the list of machine instructions for
     * the program represented by the tree.
     *
     * @param out where to print the Soros instruction list
     */
    public void compileTo( PrintWriter out ) {
        this.program.compile(out);
    }
}
