package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
public class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(new BookingController(bookingService)).build();
    }

    @Test
    void testCreateBooking() throws Exception {
        BookingInputDto bookingInputDto = new BookingInputDto(1L, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2));
        BookingDto bookingDto = new BookingDto(1L, bookingInputDto.getStart(), bookingInputDto.getEnd(), null, null, null);
        when(bookingService.create(any(BookingInputDto.class), anyLong())).thenReturn(bookingDto);

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingInputDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateBooking() throws Exception {
        BookingDto bookingDto = new BookingDto(1L, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2), null, null, null);
        when(bookingService.update(anyLong(), anyLong(), anyBoolean())).thenReturn(bookingDto);

        mockMvc.perform(patch("/bookings/1")
                        .param("approved", "true")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void testGetBookingById() throws Exception {
        BookingDto bookingDto = new BookingDto(1L, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2), null, null, null);
        when(bookingService.getBookingById(1L, 1L)).thenReturn(bookingDto);

        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void testGetBookings() throws Exception {
        BookingDto bookingDto1 = new BookingDto(1L, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2), null, null, null);
        BookingDto bookingDto2 = new BookingDto(2L, LocalDateTime.now().plusHours(3), LocalDateTime.now().plusHours(4), null, null, null);
        List<BookingDto> bookings = Arrays.asList(bookingDto1, bookingDto2);
        when(bookingService.getBookings(anyString(), anyLong())).thenReturn(bookings);

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void testGetBookingsOwner() throws Exception {
        BookingDto bookingDto1 = new BookingDto(1L, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2), null, null, null);
        BookingDto bookingDto2 = new BookingDto(2L, LocalDateTime.now().plusHours(3), LocalDateTime.now().plusHours(4), null, null, null);
        List<BookingDto> bookings = Arrays.asList(bookingDto1, bookingDto2);
        when(bookingService.getBookingsOwner(anyString(), anyLong())).thenReturn(bookings);

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", "1")
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    /*@Test
    void testGetLastBooking() throws Exception {
        BookingShortDto bookingShortDto = new BookingShortDto(1L, 1L, LocalDateTime.now().minusHours(2), LocalDateTime.now().minusHours(1));
        when(bookingService.getLastBooking(1L)).thenReturn(bookingShortDto);

        mockMvc.perform(get("/bookings/last/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }*/

    /*@Test
    void testGetNextBooking() throws Exception {
        BookingShortDto bookingShortDto = new BookingShortDto(1L, 1L, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2));
        when(bookingService.getNextBooking(1L)).thenReturn(bookingShortDto);

        mockMvc.perform(get("/bookings/next/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }*/
}
