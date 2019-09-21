package calculator;

/**
 * Represents a set of requirements for function.
 * */
public abstract class Function {

    /**
     * @return notation of the function.
     * */
    public abstract String getNotation();

    /**
     * @param x argument of the function.
     * @return value of the function.
     * */
    public abstract double compute(double x);
}