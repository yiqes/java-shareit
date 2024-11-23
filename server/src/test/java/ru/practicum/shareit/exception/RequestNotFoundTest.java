package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class RequestNotFoundTest {

    @Test
    void testRequestNotFoundException() {
        String errorMessage = "Request not found";
        RequestNotFoundException exception = new RequestNotFoundException(errorMessage);

        assertEquals(errorMessage, exception.getMessage());
    }

    @Test
    void testRequestNotFoundExceptionLogging() {
        String errorMessage = "Request not found";
        Logger logger = LoggerFactory.getLogger(RequestNotFoundException.class);

        try (MockedStatic<LoggerFactory> mockedLoggerFactory = mockStatic(LoggerFactory.class)) {
            Logger mockLogger = mock(Logger.class);
            mockedLoggerFactory.when(() -> LoggerFactory.getLogger(RequestNotFoundException.class)).thenReturn(mockLogger);

            new RequestNotFoundException(errorMessage);
        }
    }
}
