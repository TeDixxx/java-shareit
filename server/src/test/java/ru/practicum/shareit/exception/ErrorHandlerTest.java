package ru.practicum.shareit.exception;


import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exceptions.*;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;


public class ErrorHandlerTest {


    private final ErrorHandler errorHandler = new ErrorHandler();


    @Test
    void handleItemNotFoundException() {
        ItemNotFoundException exception = new ItemNotFoundException("Not found");
        Map<String, String> result = errorHandler.handleItemNotFoundException(exception);
        assertEquals("Not found", result.get("Not found"));
    }

    @Test
    public void handleIncorrectDataException() {
        IncorrectDataException exception = new IncorrectDataException("Incorrect data");
        Map<String, String> result = errorHandler.handleIncorrectDataException(exception);
        assertEquals("Incorrect data", result.get("Incorrect data"));
    }

    @Test
    public void handleUnknownStateException() {
        UnknownStateException exception = new UnknownStateException("error");
        Map<String, String> result = errorHandler.handleUnknownStateException(exception);
        assertEquals("error", result.get("error"));
    }

    @Test
    public void handleUnAvailableException() {
        UnAvailableException exception = new UnAvailableException("Unavailable error");
        Map<String, String> result = errorHandler.handleUnAvailableException(exception);
        assertEquals("Unavailable error", result.get("Unavailable error"));
    }

    @Test
    public void handleBookingNotFoundException() {
        BookingNotFoundException exception = new BookingNotFoundException("Booking not found");
        Map<String, String> result = errorHandler.handleBookingNotFoundException(exception);
        assertEquals("Booking not found", result.get("Booking not found"));
    }

    @Test
    public void handleUserNotFoundException() {
        UserNotFoundException exception = new UserNotFoundException("Not found");
        Map<String, String> result = errorHandler.handleUserNotFoundException(exception);
        assertEquals("Not found", result.get("Not found"));
    }

    @Test
    public void handleItemRequestNotFoundException() {
        ItemRequestNotFoundException exception = new ItemRequestNotFoundException("Request not found!");
        Map<String, String> result = errorHandler.handleItemRequestNotFoundException(exception);
        assertEquals("Request not found!", result.get("Request not found!"));
    }
}
