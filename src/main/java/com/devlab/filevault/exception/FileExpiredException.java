package com.devlab.filevault.exception;

/**
 * Custom exception to be thrown when a file is not found.
 * This exception extends RuntimeException, allowing it to be thrown
 * without being declared in method signatures.
 */
public class FileExpiredException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "File has expired";

    /**
     * Constructor for FileExpiredException with a custom message.
     *
     * @param message The custom message to be associated with the exception.
     */
    public FileExpiredException(String message) {
        super(message);
    }

    /**
     * Default constructor for FileExpiredException.
     * Uses a default message.
     */
    public FileExpiredException() {
        super(DEFAULT_MESSAGE);
    }
}
