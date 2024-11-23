package ru.practicum.shareit.util;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.ValidationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PaginationTest {

    @Test
    void testPaginationWithValidFromAndSize() {
        Pagination pagination = new Pagination(10, 20);
        System.out.println("Actual page size: " + pagination.getPageSize());
        System.out.println("Actual index: " + pagination.getIndex());
        System.out.println("Actual total pages: " + pagination.getTotalPages());
        assertEquals(10, pagination.getPageSize());
        assertEquals(1, pagination.getIndex());
        assertEquals(3, pagination.getTotalPages());
    }

    @Test
    void testPaginationWithFromEqualToSize() {
        Pagination pagination = new Pagination(10, 10);
        assertEquals(10, pagination.getPageSize());
        assertEquals(1, pagination.getIndex());
        assertEquals(2, pagination.getTotalPages());
    }

    @Test
    void testPaginationWithFromEqualToZero() {
        Pagination pagination = new Pagination(0, 20);
        assertEquals(20, pagination.getPageSize());
        assertEquals(0, pagination.getIndex());
        assertEquals(1, pagination.getTotalPages());
    }

    @Test
    void testPaginationWithSizeEqualToZero() {
        assertThrows(ValidationException.class, () -> new Pagination(10, 0));
    }

    @Test
    void testPaginationWithNegativeFrom() {
        assertThrows(ValidationException.class, () -> new Pagination(-10, 20));
    }

    @Test
    void testPaginationWithNegativeSize() {
        assertThrows(ValidationException.class, () -> new Pagination(10, -20));
    }

    @Test
    void testPaginationWithNullSize() {
        Pagination pagination = new Pagination(10, null);
        assertEquals(10, pagination.getPageSize());
        assertEquals(1, pagination.getIndex());
        assertEquals(0, pagination.getTotalPages());
    }

    @Test
    void testPaginationWithFromEqualToZeroAndNullSize() {
        Pagination pagination = new Pagination(0, null);
        assertEquals(1000, pagination.getPageSize());
        assertEquals(0, pagination.getIndex());
        assertEquals(0, pagination.getTotalPages());
    }
}