package ru.practicum.shareit.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.CommentDto;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ValidationService {
    UserService userService;
    ItemService itemService;
    BookingService bookingService;

    @Autowired
    public ValidationService(UserService userService, ItemService itemService, BookingService bookingService) {
        this.userService = userService;
        this.itemService = itemService;
        this.bookingService = bookingService;
    }

    public boolean isExistUser(Long userId) {
        boolean exist = false;
        if (userService.getUser(userId) != null) {
            exist = true;
        }
        return exist;
    }

    public boolean isAvailableItem(Long itemId) {
        return itemService.findItemById(itemId).getAvailable();
    }

    public boolean isItemOwner(Long itemId, Long userId) {

        return itemService.getItemsByOwner(userId).stream()
                .anyMatch(i -> i.getId().equals(itemId));
    }

    public User findUserById(Long userId) {
        return userService.findUserById(userId);
    }


    public BookingShortDto getLastBooking(Long itemId) {
        return bookingService.getLastBooking(itemId);
    }

    public BookingShortDto getNextBooking(Long itemId) {
        return bookingService.getNextBooking(itemId);
    }

    public Booking getBookingWithUserBookedItem(Long itemId, Long userId) {
        return bookingService.getBookingWithUserBookedItem(itemId, userId);
    }

    public List<CommentDto> getCommentsByItemId(Long itemId) {
        return itemService.getCommentsByItemId(itemId);
    }
}
