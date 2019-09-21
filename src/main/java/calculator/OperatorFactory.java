package calculator;

class OperatorFactory {

    private OperatorFactory() {
    }

    public static Operator CreateBinaryOperator(char operatorSymbol) {

        switch (operatorSymbol) {
            case '+':
                return new Addition();
            case '-':
                return new Subtraction();
            case '*':
                return new Multiplication();
            case '/':
                return new Division();
            default:
                return null;
        }
    }

    public static Operator CreateUnaryOperator(char operatorSymbol) {
        if (operatorSymbol == '-') {
            return new NumericNegation();
        }
        return null;
    }

    private static class Multiplication extends BinaryOperator {
        @Override
        public double compute(double x, double y) {
            return x * y;
        }

        @Override
        public byte getPriority() {
            return 1;
        }
    }

    private static class Division extends BinaryOperator {
        @Override
        public double compute(double x, double y) {
            return x / y;
        }

        @Override
        public byte getPriority() {
            return 1;
        }
    }

    private static class Addition extends BinaryOperator {
        @Override
        public double compute(double x, double y) {
            return x + y;
        }

        @Override
        public byte getPriority() {
            return 0;
        }
    }

    private static class Subtraction extends BinaryOperator {
        @Override
        public double compute(double x, double y) {
            return x - y;
        }

        @Override
        public byte getPriority() {
            return 0;
        }
    }

    private static class NumericNegation extends UnaryOperator {
        @Override
        public double compute(double x) {
            return -x;
        }

        @Override
        protected byte getPriority() {
            return 2;
        }
    }
}