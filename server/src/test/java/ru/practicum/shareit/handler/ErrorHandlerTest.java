package ru.practicum.shareit.handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.practicum.shareit.exception.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class ErrorHandlerTest {

    private ErrorHandler errorHandler;

    @BeforeEach
    void setUp() {
        errorHandler = new ErrorHandler();
    }

    @Test
    void testHandleUserNotFoundException() {
        UserNotFoundException exception = new UserNotFoundException("User not found");
        ErrorResponse response = errorHandler.handleUserNotFoundException(exception);

        assertEquals("User not found", response.getError());
    }

    @Test
    void testHandleItemNotFoundException() {
        ItemNotFoundException exception = new ItemNotFoundException("Item not found");
        ErrorResponse response = errorHandler.handleItemNotFoundException(exception);

        assertEquals("Item not found", response.getError());
    }

    @Test
    void testHandleBookingNotFoundException() {
        BookingNotFoundException exception = new BookingNotFoundException("Booking not found");
        ErrorResponse response = errorHandler.handleBookingNotFoundException(exception);

        assertEquals("Booking not found", response.getError());
    }

    @Test
    void testHandleValidationException() {
        ValidationException exception = new ValidationException("Validation failed");
        ErrorResponse response = errorHandler.handleValidationException(exception);

        assertEquals("Validation failed", response.getError());
    }

    @Test
    void testHandleMethodArgumentNotValidationException() {
        MethodArgumentNotValidException exception = Mockito.mock(MethodArgumentNotValidException.class);
        when(exception.getMessage()).thenReturn("Аргумент не прошел валидацию!");
        ErrorResponse response = errorHandler.handleMethodArgumentNotValidationException(exception);

        assertEquals("Аргумент не прошел валидацию!", response.getError());
    }

    @Test
    void testHandleUserAlreadyExistException() {
        UserAlreadyExistsException exception = new UserAlreadyExistsException("User already exists");
        ErrorResponse response = errorHandler.handleUserAlreadyExistException(exception);

        assertEquals("User already exists", response.getError());
    }
}
