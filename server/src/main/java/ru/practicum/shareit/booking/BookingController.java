package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;

import java.util.List;

/**
 * The type Booking controller.
 */
@Slf4j
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private static final String USER_ID = "X-Sharer-User-Id";
    private final BookingService service;
    private static final String BOOKING_ID = "{booking-id}";

    /**
     * Instantiates a new Booking controller.
     *
     * @param bookingService the booking service
     */
    @Autowired
    public BookingController(BookingService bookingService) {
        this.service = bookingService;
    }

    /**
     * Create booking dto.
     *
     * @param bookingInputDto the booking input dto
     * @param bookerId        the booker id
     * @return the booking dto
     */
    @ResponseBody
    @PostMapping
    public BookingDto create(@RequestBody BookingInputDto bookingInputDto,
                             @RequestHeader(USER_ID) Long bookerId) {
        log.info("Получен POST-запрос к эндпоинту: '/bookings' " +
                "на создание бронирования от пользователя с ID={}", bookerId);
        return service.create(bookingInputDto, bookerId);
    }

    /**
     * Update booking dto.
     *
     * @param bookingId the booking id
     * @param userId    the user id
     * @param approved  the approved
     * @return the booking dto
     */
    @ResponseBody
    @PatchMapping(BOOKING_ID)
    public BookingDto update(@PathVariable("booking-id") Long bookingId,
                             @RequestHeader(USER_ID) Long userId, @RequestParam Boolean approved) {
        log.info("Получен PATCH-запрос к эндпоинту: '/bookings' на обновление статуса бронирования с ID={}", bookingId);
        return service.update(bookingId, userId, approved);
    }

    /**
     * Gets booking by id.
     *
     * @param bookingId the booking id
     * @param userId    the user id
     * @return the booking by id
     */
    @GetMapping(BOOKING_ID)
    public BookingDto getBookingById(@PathVariable("booking-id") Long bookingId, @RequestHeader(USER_ID) Long userId) {
        log.info("Получен GET-запрос к эндпоинту: '/bookings' на получение бронирования с ID={}", bookingId);
        return service.getBookingById(bookingId, userId);
    }

    /**
     * Gets bookings.
     *
     * @param state  the state
     * @param userId the user id
     * @return the bookings
     */
    @GetMapping
    public List<BookingDto> getBookings(@RequestParam(name = "state", defaultValue = "ALL") String state,
                                        @RequestHeader(USER_ID) Long userId) {
        log.info("Получен GET-запрос к эндпоинту: '/bookings' на получение " +
                "списка всех бронирований пользователя с ID={} с параметром STATE={}", userId, state);
        return service.getBookings(state, userId);
    }

    /**
     * Gets bookings owner.
     *
     * @param state  the state
     * @param userId the user id
     * @return the bookings owner
     */
    @GetMapping("/owner")
    public List<BookingDto> getBookingsOwner(@RequestParam(name = "state", defaultValue = "ALL") String state,
                                             @RequestHeader(USER_ID) Long userId) {
        log.info("Получен GET-запрос к эндпоинту: '/bookings/owner' на получение " +
                "списка всех бронирований вещей пользователя с ID={} с параметром STATE={}", userId, state);
        return service.getBookingsOwner(state, userId);
    }
}