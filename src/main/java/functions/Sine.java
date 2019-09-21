package functions;

import calculator.Function;

public class Sine extends Function {
    @Override
    public double compute(double x) {
        return Math.sin(x);
    }

    @Override
    public String getNotation() {
        return "sin";
    }
}