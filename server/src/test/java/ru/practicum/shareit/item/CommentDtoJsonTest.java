package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;

@JsonTest
public class CommentDtoJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testSerializeCommentDto() throws Exception {
        CommentDto commentDto = new CommentDto(1L, "Comment Text", null, "Booker", LocalDateTime.now().withNano(0));
        String json = objectMapper.writeValueAsString(commentDto);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

        assertEquals("{\"id\":1,\"text\":\"Comment Text\",\"authorName\":\"Booker\",\"created\":\"" + commentDto.getCreated().format(formatter) + "\"}", json);
    }

    @Test
    void testDeserializeCommentDto() throws Exception {
        String json = "{\"id\":1,\"text\":\"Comment Text\",\"authorName\":\"Booker\",\"created\":\"2023-10-01T00:00:00\"}";
        CommentDto commentDto = objectMapper.readValue(json, CommentDto.class);

        assertEquals(1L, commentDto.getId());
        assertEquals("Comment Text", commentDto.getText());
        assertEquals("Booker", commentDto.getAuthorName());
        assertEquals(LocalDateTime.parse("2023-10-01T00:00:00"), commentDto.getCreated());
    }
}