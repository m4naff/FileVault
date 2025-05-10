package com.devlab.filevault.exception;

/**
 * Custom exception to be thrown when a file is not found.
 * This exception extends RuntimeException, allowing it to be thrown
 * without being declared in method signatures.
 */
public class InvalidPasswordException extends RuntimeException{

    private static final String DEFAULT_MESSAGE = "Invalid password";

    /**
     * Constructor for InvalidPasswordException with a custom message.
     *
     * @param message The custom message to be associated with the exception.
     */
    public InvalidPasswordException(String message) {
        super(message);
    }

    /**
     * Default constructor for InvalidPasswordException.
     * Uses a default message.
     */
    public InvalidPasswordException() {
        super(DEFAULT_MESSAGE);
    }
}
