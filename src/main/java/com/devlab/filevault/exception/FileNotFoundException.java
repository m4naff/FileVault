package com.devlab.filevault.exception;

/**
 * Custom exception to be thrown when a file is not found.
 * This exception extends RuntimeException, allowing it to be thrown
 * without being declared in method signatures.
 */
public class FileNotFoundException extends RuntimeException{

    private static final String DEFAULT_MESSAGE = "File not found";

    /**
     * Constructor for FileNotFoundException with a custom message.
     *
     * @param message The custom message to be associated with the exception.
     */
    public FileNotFoundException(String message) {
        super(message);
    }

    /**
     * Default constructor for FileNotFoundException.
     * Uses a default message.
     */
    public FileNotFoundException() {
        super(DEFAULT_MESSAGE);
    }

}
