package calculator;

public final class UnknownWordException extends IllegalArgumentException {

    private final String word;
    private final int index;

    UnknownWordException(String word, int index) {
        this.word = word;
        this.index = index;
    }

    @Override
    public String getMessage() {
        return "Unknown word: <" + word + "> in position " + index;
    }

    public String getWord() {
        return word;
    }

    public int getIndex() {
        return index;
    }
}