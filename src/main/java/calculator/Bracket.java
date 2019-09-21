package calculator;

class Bracket implements Token {
    private final boolean open;

    Bracket(boolean open) {
        this.open = open;
    }

    boolean isOpen() {
        return this.open;
    }
}