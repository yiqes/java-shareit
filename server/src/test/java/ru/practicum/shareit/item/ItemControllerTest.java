package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
public class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(new ItemController(itemService)).build();
    }

    @Test
    void testCreateItem() throws Exception {
        ItemDto itemDto = new ItemDto(null, "Item Name", "Item Description", true, null, null, null, null, null);
        when(itemService.create(any(ItemDto.class), anyLong())).thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Item Name"))
                .andExpect(jsonPath("$.description").value("Item Description"));
    }

    @Test
    void testGetItemById() throws Exception {
        ItemDto itemDto = new ItemDto(1L, "Item Name", "Item Description", true, null, null, null, null, null);
        when(itemService.getItemById(1L, 1L)).thenReturn(itemDto);

        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Item Name"))
                .andExpect(jsonPath("$.description").value("Item Description"));
    }

    @Test
    void testGetItemsByOwner() throws Exception {
        ItemDto itemDto1 = new ItemDto(1L, "Item 1", "Description 1", true, null, null, null, null, null);
        ItemDto itemDto2 = new ItemDto(2L, "Item 2", "Description 2", true, null, null, null, null, null);
        List<ItemDto> items = Arrays.asList(itemDto1, itemDto2);
        when(itemService.getItemsByOwner(1L)).thenReturn(items);

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void testDeleteItem() throws Exception {
        doNothing().when(itemService).delete(1L, 1L);

        mockMvc.perform(delete("/items/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());
    }

    @Test
    void testGetItemsBySearchQuery() throws Exception {
        ItemDto itemDto = new ItemDto(1L, "Item Name", "Item Description", true, null, null, null, null, null);
        when(itemService.getItemsBySearchQuery("Item")).thenReturn(Arrays.asList(itemDto));

        mockMvc.perform(get("/items/search")
                        .param("text", "Item"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void testUpdateItem() throws Exception {
        ItemDto itemDto = new ItemDto(1L, "Updated Name", "Updated Description", false, null, null, null, null, null);
        when(itemService.update(any(ItemDto.class), eq(1L), eq(1L))).thenReturn(itemDto);

        mockMvc.perform(patch("/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.description").value("Updated Description"));
    }

    @Test
    void testCreateComment() throws Exception {
        CommentDto commentDto = new CommentDto(null, "Comment Text", null, "Booker", LocalDateTime.now());
        when(itemService.createComment(any(CommentDto.class), eq(1L), eq(1L))).thenReturn(commentDto);

        mockMvc.perform(post("/items/1/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto))
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("Comment Text"));
    }

    @Test
    void testGetCommentsByItemId() throws Exception {
        CommentDto commentDto1 = new CommentDto(1L, "Comment 1", null, "Booker", LocalDateTime.now());
        CommentDto commentDto2 = new CommentDto(2L, "Comment 2", null, "Booker", LocalDateTime.now().plusDays(1));
        List<CommentDto> comments = Arrays.asList(commentDto1, commentDto2);
        when(itemService.getCommentsByItemId(1L)).thenReturn(comments);

        mockMvc.perform(get("/items/1/comments")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetByRequestId() throws Exception {
        ItemDto itemDto1 = new ItemDto(1L, "Item 1", "Description 1", true, null, null, null, null, null);
        ItemDto itemDto2 = new ItemDto(2L, "Item 2", "Description 2", true, null, null, null, null, null);
        List<ItemDto> items = Arrays.asList(itemDto1, itemDto2);
        when(itemService.getByRequestId(1L)).thenReturn(items);

        mockMvc.perform(get("/items/request/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isNotFound());
    }
}
