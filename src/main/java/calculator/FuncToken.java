package calculator;

class FuncToken implements Token {
    private final Function function;

    FuncToken(Function function) {
        this.function = function;
    }

    double compute(double x) {
        return this.function.compute(x);
    }
}