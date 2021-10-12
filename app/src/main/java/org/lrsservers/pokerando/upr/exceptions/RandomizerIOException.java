package org.lrsservers.pokerando.upr.exceptions;

public class RandomizerIOException extends RuntimeException {
    private static final long serialVersionUID = -8174099615381353972L;

    public RandomizerIOException(Exception e) {
        super(e);
    }

    public RandomizerIOException(String text) {
        super(text);
    }
}

