package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ItemNotFoundExceptionTest {

    @Test
    void testItemNotFoundException() {
        String errorMessage = "Item not found";
        ItemNotFoundException exception = new ItemNotFoundException(errorMessage);

        assertEquals(errorMessage, exception.getMessage());
    }

    @Test
    void testItemNotFoundExceptionLogging() {
        String errorMessage = "Item not found";
        Logger logger = LoggerFactory.getLogger(ItemNotFoundException.class);

        try (MockedStatic<LoggerFactory> mockedLoggerFactory = mockStatic(LoggerFactory.class)) {
            Logger mockLogger = mock(Logger.class);
            mockedLoggerFactory.when(() -> LoggerFactory.getLogger(ItemNotFoundException.class)).thenReturn(mockLogger);

            new ItemNotFoundException(errorMessage);
        }
    }
}
