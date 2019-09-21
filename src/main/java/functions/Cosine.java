package functions;

import calculator.Function;

public class Cosine extends Function {
    @Override
    public double compute(double x) {
        return Math.cos(x);
    }

    @Override
    public String getNotation() {
        return "cos";
    }
}