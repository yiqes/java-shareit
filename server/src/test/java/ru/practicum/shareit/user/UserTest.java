package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    @Test
    void testUserConstructor() {
        User user = new User(1L, "email@example.com", "John Doe", Instant.now());
        assertEquals(1L, user.getId());
        assertEquals("email@example.com", user.getEmail());
        assertEquals("John Doe", user.getName());
        assertNotNull(user.getRegistrationDate());
    }

    @Test
    void testUserEquals() {
        Instant timing = Instant.now();
        User user1 = new User(1L, "email@example.com", "John Doe", timing);
        User user2 = new User(1L, "email@example.com", "John Doe", timing);
        User user3 = new User(2L, "email@example.com", "John Doe", Instant.now());
        User user4 = new User(3L, "other@example.com", "John Doe", Instant.now());
        User user5 = new User(1L, "email@example.com", "Jane Doe", Instant.now());

        assertEquals(user1, user2);
        assertEquals(user2, user1);
        assertEquals(user1, user1);
        assertNotEquals(null, user1);
        assertNotEquals(user1, new Object());
        assertNotEquals(user1, user3);
        assertNotEquals(user1, user4);
        assertNotEquals(user1, user5);
    }

    @Test
    void testUserHashCode() {
        Instant timing = Instant.now();

        User user1 = new User(1L, "email@example.com", "John Doe", timing);
        User user2 = new User(1L, "email@example.com", "John Doe", timing);
        User user3 = new User(2L, "email@3example.com", "John3 Doe", Instant.now());
        User user4 = new User(1L, "other@example.com", "John Doe", Instant.now());
        User user5 = new User(1L, "email@example.com", "Jane Doe", Instant.now());
        System.out.println(user1.hashCode() + "           " + user3.hashCode() + "    " + user2.hashCode());
        assertEquals(user1.hashCode(), user2.hashCode());
        assertNotEquals(user1.hashCode(), user3.hashCode());
        assertNotEquals(user1.hashCode(), user4.hashCode());
        assertNotEquals(user1.hashCode(), user5.hashCode());
    }
}