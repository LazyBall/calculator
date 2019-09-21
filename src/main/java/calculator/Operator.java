package calculator;

abstract class Operator implements Token {
    protected abstract byte getPriority();
}

abstract class BinaryOperator extends Operator {
    public abstract double compute(double x, double y);
}

abstract class UnaryOperator extends Operator {
    public abstract double compute(double x);
}