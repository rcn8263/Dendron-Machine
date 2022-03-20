package dendron.treenodes;

import dendron.Errors;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * A subclass of ExpressionNode that represents a calculation that is
 * represented by a binary operator and two operands

 * @author Ryan Nowak
 */
public class BinaryOperation implements ExpressionNode {
    static String ADD = "+";
    static String DIV = "/";
    static String MUL = "*";
    static Collection<String> OPERATORS;
    static String SUB = "-";
    private String operator;
    private ExpressionNode left;
    private ExpressionNode right;

    /**
     * Creates a new BinaryOperation node
     * @param operator A string of the operator
     * @param leftChild The left operand
     * @param rightChild The right operand
     */
    public BinaryOperation(String operator, ExpressionNode leftChild,
                                            ExpressionNode rightChild) {
        this.operator = operator;
        this.left = leftChild;
        this.right = rightChild;
    }

    @Override
    public void infixDisplay() {
        System.out.print("( ");
        left.infixDisplay();
        System.out.print(" " + this.operator + " ");
        right.infixDisplay();
        System.out.print(" )");
    }

    @Override
    public void compile(PrintWriter out) {
        left.compile(out);
        right.compile(out);
        if (operator == ADD) {
            out.println("ADD ");
        }
        else if (operator == SUB) {
            out.println("SUB ");
        }
        else if (operator == MUL) {
            out.println("MUL ");
        }
        else if (operator == DIV) {
            out.println("DIV ");
        }
    }

    @Override
    public int evaluate(Map<String, Integer> symTab) {
        int result = 0;
        if (operator == ADD) {
            result = left.evaluate(symTab) + right.evaluate(symTab);
        }
        else if (operator == SUB) {
            result = left.evaluate(symTab) - right.evaluate(symTab);
        }
        else if (operator == MUL) {
            result = left.evaluate(symTab) * right.evaluate(symTab);
        }
        else if (operator == DIV) {
            if (right.evaluate(symTab) == 0) {
                Errors.report(Errors.Type.DIVIDE_BY_ZERO, null);
            }
            result = left.evaluate(symTab) / right.evaluate(symTab);
        }
        return result;
    }
}
