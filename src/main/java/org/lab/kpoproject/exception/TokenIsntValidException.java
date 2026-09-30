package org.lab.kpoproject.exception;

public class TokenIsntValidException extends RuntimeException {
    public TokenIsntValidException(final String message) {
        super(message);
    }
}
