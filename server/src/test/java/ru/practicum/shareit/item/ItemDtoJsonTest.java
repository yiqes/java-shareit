package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@JsonTest
public class ItemDtoJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testSerializeItemDto() throws Exception {
        ItemDto itemDto = new ItemDto(1L, "Item Name", "Item Description", true, null, null, null, null, null);
        String json = objectMapper.writeValueAsString(itemDto);

        assertEquals("{\"id\":1,\"name\":\"Item Name\",\"description\":\"Item Description\",\"available\":true,\"requestId\":null,\"lastBooking\":null,\"nextBooking\":null,\"comments\":null}", json);
    }

    @Test
    void testDeserializeItemDto() throws Exception {
        String json = "{\"id\":1,\"name\":\"Item Name\",\"description\":\"Item Description\",\"available\":true,\"requestId\":null,\"lastBooking\":null,\"nextBooking\":null,\"comments\":null}";
        ItemDto itemDto = objectMapper.readValue(json, ItemDto.class);

        assertEquals(1L, itemDto.getId());
        assertEquals("Item Name", itemDto.getName());
        assertEquals("Item Description", itemDto.getDescription());
        assertEquals(true, itemDto.getAvailable());
    }
}
