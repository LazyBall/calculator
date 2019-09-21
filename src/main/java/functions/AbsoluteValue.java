package functions;

import calculator.Function;

public class AbsoluteValue extends Function {
    @Override
    public double compute(double x) {
        return Math.abs(x);
    }

    @Override
    public String getNotation() {
        return "abs";
    }
}