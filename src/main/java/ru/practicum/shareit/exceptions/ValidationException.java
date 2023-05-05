package ru.practicum.shareit.exceptions;

import java.io.IOException;

public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
