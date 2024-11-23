package ru.practicum.shareit.request;

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

@WebMvcTest(RequestController.class)
public class RequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RequestService requestService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(new RequestController(requestService)).build();
    }

    @Test
    void testCreateRequest() throws Exception {
        RequestDto requestDto = new RequestDto(null, "Test Request", null, LocalDateTime.now(), null);
        when(requestService.saveRequest(any(RequestDto.class), anyLong(), any(LocalDateTime.class))).thenReturn(requestDto);

        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetRequestById() throws Exception {
        RequestDto requestDto = new RequestDto(1L, "Test Request", null, LocalDateTime.now(), null);
        when(requestService.getRequestById(1L, 1L)).thenReturn(requestDto);

        mockMvc.perform(get("/requests/1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetOwnRequests() throws Exception {
        RequestDto requestDto1 = new RequestDto(1L, "Test Request 1", null, LocalDateTime.now(), null);
        RequestDto requestDto2 = new RequestDto(2L, "Test Request 2", null, LocalDateTime.now(), null);
        List<RequestDto> requests = Arrays.asList(requestDto1, requestDto2);
        when(requestService.getOwnRequests(1L)).thenReturn(requests);

        mockMvc.perform(get("/requests"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetAllRequests() throws Exception {
        RequestDto requestDto1 = new RequestDto(1L, "Test Request 1", null, LocalDateTime.now(), null);
        RequestDto requestDto2 = new RequestDto(2L, "Test Request 2", null, LocalDateTime.now(), null);
        List<RequestDto> requests = Arrays.asList(requestDto1, requestDto2);
        when(requestService.getAllRequests(1L, 0, 10)).thenReturn(requests);

        mockMvc.perform(get("/requests/all")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isBadRequest());
    }
}
