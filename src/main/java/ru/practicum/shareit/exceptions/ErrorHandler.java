package ru.practicum.shareit.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleItemNotFoundException(final ItemNotFoundException e) {
        return Map.of("Not found", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleIncorrectDataException(final  IncorrectDataException e) {
        return Map.of("Incorrect data", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleUnknownStateException(final UnknownStateException e) {
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleUnAvailableException(final  UnAvailableException e) {
        return Map.of("Unavailable error", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleBookingNotFoundException(final  BookingNotFoundException e) {
        return Map.of("Booking not found", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleUserNotFoundException(final UserNotFoundException e) {
        return Map.of("Not found", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidException(final MethodArgumentNotValidException e) {
        return Map.of("Unavailable error", e.getMessage());
    }

}
