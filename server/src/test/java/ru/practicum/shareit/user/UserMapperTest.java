package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserMapperTest {

    private UserMapper userMapper = new UserMapper();

    @Test
    void testToUserDto() {
        // Arrange
        User user = new User(1L, "email@example.com", "John Doe", Instant.now());

        // Act
        UserDto userDto = userMapper.toUserDto(user);

        // Assert
        assertEquals(user.getId(), userDto.getId());
        assertEquals(user.getEmail(), userDto.getEmail());
        assertEquals(user.getName(), userDto.getName());
        assertEquals(user.getRegistrationDate(), userDto.getRegistrationDate());
    }

    @Test
    void testToUser() {
        // Arrange
        UserDto userDto = new UserDto(1L, "email@example.com", "John Doe", Instant.now());

        // Act
        User user = userMapper.toUser(userDto);

        // Assert
        assertEquals(userDto.getId(), user.getId());
        assertEquals(userDto.getEmail(), user.getEmail());
        assertEquals(userDto.getName(), user.getName());
        assertEquals(userDto.getRegistrationDate(), user.getRegistrationDate());
    }
}