package calculator;

class Number implements Token {
    private final double value;

    Number(double value) {
        this.value = value;
    }

    double getValue() {
        return this.value;
    }
}