package com.devlab.filevault.exception;

import com.devlab.filevault.model.common.CustomError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler for the application.
 * This class is annotated with @RestControllerAdvice, which allows it to handle exceptions
 * thrown by any controller in the application.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<CustomError> handleFileNotFoundException(FileNotFoundException ex) {
        CustomError error = CustomError.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .header(CustomError.Header.NOT_FOUND.getName())
                .message(ex.getMessage())
                .isSuccess(false)
                .build();
        return new ResponseEntity<>(error, error.getHttpStatus());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<CustomError> handleRuntimeException(RuntimeException ex) {
        CustomError error = CustomError.builder()
                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .header(CustomError.Header.DATABASE_ERROR.getName())
                .message(ex.getMessage())
                .isSuccess(false)
                .build();
        return new ResponseEntity<>(error, error.getHttpStatus());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<CustomError> handleIllegalArgumentException(IllegalArgumentException ex) {
        CustomError error = CustomError.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .header(CustomError.Header.VALIDATION_ERROR.getName())
                .message(ex.getMessage())
                .isSuccess(false)
                .build();
        return new ResponseEntity<>(error, error.getHttpStatus());
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<CustomError> handleInvalidPasswordException(InvalidPasswordException ex) {
        CustomError error = CustomError.builder()
                .httpStatus(HttpStatus.UNAUTHORIZED)
                .header(CustomError.Header.AUTH_ERROR.getName())
                .message(ex.getMessage())
                .isSuccess(false)
                .build();
        return new ResponseEntity<>(error, error.getHttpStatus());
    }

}
