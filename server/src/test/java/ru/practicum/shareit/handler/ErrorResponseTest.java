package ru.practicum.shareit.handler;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ErrorResponseTest {

    @Test
    void testErrorResponse() {
        String errorMessage = "Test error message";
        ErrorResponse errorResponse = new ErrorResponse(errorMessage);

        assertEquals(errorMessage, errorResponse.getError());
    }
}
