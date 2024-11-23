package ru.practicum.shareit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.CommentDto;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ValidationServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private ItemService itemService;

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private ValidationService validationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testIsExistUser() {
        Long userId = 1L;
        when(userService.getUser(userId))
                .thenReturn(Mockito.mock(UserDto.class));

        boolean result = validationService.isExistUser(userId);

        assertTrue(result);
    }

    @Test
    void testIsAvailableItem() {
        Long itemId = 1L;
        Item item = new Item();
        item.setAvailable(true);
        when(itemService.findItemById(itemId)).thenReturn(item);

        boolean result = validationService.isAvailableItem(itemId);

        assertTrue(result);
    }

    @Test
    void testIsItemOwner() {
        Long itemId = 1L;
        Long userId = 1L;
        ItemDto itemDto = new ItemDto();
        itemDto.setId(itemId);
        when(itemService.getItemsByOwner(userId)).thenReturn(Arrays.asList(itemDto));

        boolean result = validationService.isItemOwner(itemId, userId);

        assertTrue(result);
    }

    @Test
    void testFindUserById() {
        Long userId = 1L;
        User user = new User();
        when(userService.findUserById(userId)).thenReturn(user);

        User result = validationService.findUserById(userId);

        assertSame(user, result);
    }

    @Test
    void testGetLastBooking() {
        Long itemId = 1L;
        BookingShortDto bookingShortDto = new BookingShortDto(1L, 1L, LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2));
        when(bookingService.getLastBooking(itemId)).thenReturn(bookingShortDto);

        BookingShortDto result = validationService.getLastBooking(itemId);

        assertSame(bookingShortDto, result);
    }

    @Test
    void testGetNextBooking() {
        Long itemId = 1L;
        BookingShortDto bookingShortDto = new BookingShortDto(1L, 1L, LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2));
        when(bookingService.getNextBooking(itemId)).thenReturn(bookingShortDto);

        BookingShortDto result = validationService.getNextBooking(itemId);

        assertSame(bookingShortDto, result);
    }

    @Test
    void testGetBookingWithUserBookedItem() {
        Long itemId = 1L;
        Long userId = 1L;
        Booking booking = new Booking();
        when(bookingService.getBookingWithUserBookedItem(itemId, userId)).thenReturn(booking);

        Booking result = validationService.getBookingWithUserBookedItem(itemId, userId);

        assertSame(booking, result);
    }

    @Test
    void testGetCommentsByItemId() {
        Long itemId = 1L;
        List<CommentDto> comments = Arrays.asList(new CommentDto(), new CommentDto());
        when(itemService.getCommentsByItemId(itemId)).thenReturn(comments);

        List<CommentDto> result = validationService.getCommentsByItemId(itemId);

        assertSame(comments, result);
    }
}
