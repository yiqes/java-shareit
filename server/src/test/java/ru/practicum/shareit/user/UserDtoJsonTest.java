package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

@JsonTest
public class UserDtoJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testSerialize() throws Exception {
        UserDto userDto = new UserDto(1L, "test@example.com", "Test User", Instant.now());
        String json = objectMapper.writeValueAsString(userDto);

        assertEquals("{\"id\":1,\"email\":\"test@example.com\",\"name\":\"Test User\",\"registrationDate\":\"" + userDto.getRegistrationDate().toString() + "\"}", json);
    }

    @Test
    void testDeserialize() throws Exception {
        String json = "{\"id\":1,\"email\":\"test@example.com\",\"name\":\"Test User\",\"registrationDate\":\"2023-10-01T00:00:00Z\"}";
        UserDto userDto = objectMapper.readValue(json, UserDto.class);

        assertEquals(1L, userDto.getId());
        assertEquals("test@example.com", userDto.getEmail());
        assertEquals("Test User", userDto.getName());
        assertEquals(Instant.parse("2023-10-01T00:00:00Z"), userDto.getRegistrationDate());
    }
}
