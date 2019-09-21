package calculator;

import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Stack;

/**
 * Provides functionality for calculating math expressions.
 */
public class Calculator {
    private final HashMap<String, FuncToken> stringFuncMap;

    /**
     * @param functions set of math functions.
     * @throws NullPointerException functions is null or functions contains null item.
     */
    public Calculator(Function... functions) throws NullPointerException {
        if (functions == null) {
            throw new NullPointerException("functions is null.");
        }
        stringFuncMap = new HashMap<>();

        for (Function item : functions) {
            if (item == null) {
                throw new NullPointerException("functions contains null item.");
            }
            stringFuncMap.putIfAbsent(item.getNotation(), new FuncToken(item));
        }
    }

    private Iterable<Token> parseToTokens(String infixNotation){
        LinkedList<Token> tokens = new LinkedList<>();
        String word = "";

        for (int i = 0; i < infixNotation.length(); i++) {
            char symbol = infixNotation.charAt(i);
            Token token = OperatorFactory.CreateUnaryOperator(symbol);
            if (token == null || !word.isEmpty() || tokens.peekLast() instanceof Bracket &&
                    !((Bracket) tokens.peekLast()).isOpen()) {
                token = OperatorFactory.CreateBinaryOperator(symbol);
            }
            if (token == null) {
                if (symbol == '(') {
                    token = new Bracket(true);
                } else if (symbol == ')') {
                    token = new Bracket(false);
                } else {
                    word += symbol;
                    continue;
                }
            }
            if (!word.isEmpty()) {
                FuncToken function = stringFuncMap.get(word);
                if (function != null) {
                    tokens.addLast(function);
                } else {
                    try {
                        tokens.addLast(new Number(Double.parseDouble(word)));
                    } catch (NumberFormatException exception) {
                        throw new UnknownWordException(word, i-word.length());
                    }
                }
                word = "";
            }
            tokens.addLast(token);
        }

        if (!word.isEmpty()) {
            FuncToken function = stringFuncMap.get(word);
            if (function != null) {
                tokens.addLast(function);
            } else {
                try {
                    tokens.addLast(new Number(Double.parseDouble(word)));
                } catch (NumberFormatException exception) {
                    throw new UnknownWordException(word, infixNotation.length()- word.length());
                }
            }
        }
        return tokens;
    }

    /**
     * Parse infix notation to reverse polish notation (RPN).
     */
    private Iterable<Token> parseToRPN(Iterable<Token> infixNotationTokens){
        LinkedList<Token> rpn = new LinkedList<>();
        Stack<Token> stack = new Stack<>();

        for (Token token : infixNotationTokens) {
            if (token instanceof Number) {
                rpn.addLast(token);
            } else if (token instanceof BinaryOperator) {
                byte priority = ((Operator) token).getPriority();

                while (!stack.isEmpty() && stack.peek() instanceof Operator &&
                        ((Operator) stack.peek()).getPriority() >= priority) {
                    rpn.addLast(stack.pop());
                }

                stack.push(token);
            } else if (token instanceof Bracket) {
                if (((Bracket) token).isOpen()) {
                    stack.push(token);
                } else {
                    try {
                        while (!(stack.peek() instanceof Bracket)) {
                            rpn.addLast(stack.pop());
                        }

                        stack.pop();
                    } catch (EmptyStackException exception) {
                        throw new IllegalArgumentException("Mismatched brackets.");
                    }
                    if (!stack.isEmpty() && stack.peek() instanceof FuncToken) {
                        rpn.addLast(stack.pop());
                    }
                }
            } else { //FuncToken and UnaryOperator
                stack.push(token);
            }
        }

        while (!stack.isEmpty()) {
            rpn.addLast(stack.pop());
        }

        return rpn;
    }

    private double calculate(Iterable<Token> rpNotationTokens){
        Stack<Double> stack = new Stack<>();

        for (Token token : rpNotationTokens) {
            if (token instanceof Number) {
                stack.push(((Number) token).getValue());
            } else if (token instanceof BinaryOperator) {
                if (stack.size() < 2) {
                    throw new IllegalArgumentException("Mistake in the arrangement of operators or values.");
                }
                Double y = stack.pop();
                stack.push(((BinaryOperator) token).compute(stack.pop(), y));
            } else if (token instanceof UnaryOperator) {
                if (stack.isEmpty()) {
                    throw new IllegalArgumentException("Mistake in the arrangement of operators or values.");
                }
                stack.push(((UnaryOperator) token).compute(stack.pop()));
            } else if (token instanceof FuncToken) {
                if (stack.isEmpty()) {
                    throw new IllegalArgumentException("Function argument is missing.");
                }
                stack.push(((FuncToken) token).compute(stack.pop()));
            } else {
                throw new IllegalArgumentException("Mismatched brackets.");
            }
        }

        if (stack.size() != 1) {
            throw new IllegalArgumentException("Mistake in the arrangement of operators or values.");
        }

        return stack.pop();
    }

    /**
     * Calculates the value of expression
     *
     * @param infixNotation expression in infix notation.
     * @return calculated value.
     * @throws IllegalStateException expression is not correct.
     * @throws NullPointerException  infixNotation is null.
     */
    public double calculate(String infixNotation) throws IllegalArgumentException, NullPointerException {
        if (infixNotation == null) {
            throw new NullPointerException("infixNotation is null.");
        }
        if (infixNotation.isEmpty()) {
            throw new IllegalArgumentException("infixNotation is empty.");
        }
        return calculate(parseToRPN(parseToTokens(infixNotation)));
    }
}