package ru.practicum.shareit.booking;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.exception.BookingNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemServiceImpl;
import ru.practicum.shareit.service.ValidationService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserServiceImpl;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@Rollback
@DataJpaTest
@ActiveProfiles("test")
@Import({BookingMapper.class, BookingServiceImpl.class, UserServiceImpl.class, ValidationService.class,
        UserMapper.class, ItemServiceImpl.class, ItemMapper.class})
public class BookingServiceImplIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BookingServiceImpl bookingService;

    private User owner;
    private User booker;

    private Item item;

    @BeforeEach
    void setUp() {
        // Создание пользователей
        owner = new User();
        owner.setEmail("owner@example.com");
        owner.setName("Owner");
        owner.setRegistrationDate(Instant.now());
        entityManager.persist(owner);

        booker = new User();
        booker.setEmail("booker@example.com");
        booker.setName("Booker");
        booker.setRegistrationDate(Instant.now());
        entityManager.persist(booker);

        item = new Item();
        item.setName("Item");
        item.setDescription("Description");
        item.setAvailable(true);
        item.setOwner(owner);
        entityManager.persist(item);
    }

    @AfterEach
    void tearDown() {
        entityManager.clear();
    }

    @Test
    void testCreateBooking() {
        BookingInputDto bookingInputDto = new BookingInputDto(item.getId(), LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2));
        BookingDto bookingDto = bookingService.create(bookingInputDto, booker.getId());

        assertNotNull(bookingDto.getId());
        assertEquals(bookingInputDto.getStart(), bookingDto.getStart());
        assertEquals(bookingInputDto.getEnd(), bookingDto.getEnd());
    }

    @Test
    void testUpdateBooking() {
        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().plusHours(1));
        booking.setEnd(LocalDateTime.now().plusHours(2));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);
        entityManager.persist(booking);

        BookingDto updatedBookingDto = bookingService.update(booking.getId(), owner.getId(), true);

        assertEquals(BookingStatus.APPROVED, updatedBookingDto.getStatus());
    }

    @Test
    void testGetBookingById() {
        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().plusHours(1));
        booking.setEnd(LocalDateTime.now().plusHours(2));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);

        entityManager.persist(booking);

        BookingDto bookingDto = bookingService.getBookingById(booking.getId(), booker.getId());

        assertNotNull(bookingDto);
        assertEquals(booking.getId(), bookingDto.getId());
    }

    @Test
    void testGetBookings() {
        Booking booking1 = new Booking();
        booking1.setStart(LocalDateTime.now().plusHours(1));
        booking1.setEnd(LocalDateTime.now().plusHours(2));
        booking1.setItem(item);
        booking1.setBooker(booker);
        booking1.setStatus(BookingStatus.WAITING);

        Booking booking2 = new Booking();
        booking2.setStart(LocalDateTime.now().plusHours(3));
        booking2.setEnd(LocalDateTime.now().plusHours(4));
        booking2.setItem(item);
        booking2.setBooker(booker);
        booking2.setStatus(BookingStatus.WAITING);

        entityManager.persist(booking1);
        entityManager.persist(booking2);

        List<BookingDto> bookings = bookingService.getBookings("ALL", booker.getId());

        assertEquals(2, bookings.size());
    }

    @Test
    void testGetBookingsOwner() {
        Booking booking1 = new Booking();
        booking1.setStart(LocalDateTime.now().plusHours(1));
        booking1.setEnd(LocalDateTime.now().plusHours(2));
        booking1.setItem(item);
        booking1.setBooker(booker);
        booking1.setStatus(BookingStatus.WAITING);

        Booking booking2 = new Booking();
        booking2.setStart(LocalDateTime.now().plusHours(3));
        booking2.setEnd(LocalDateTime.now().plusHours(4));
        booking2.setItem(item);
        booking2.setBooker(booker);
        booking2.setStatus(BookingStatus.WAITING);

        entityManager.persist(booking1);
        entityManager.persist(booking2);

        List<BookingDto> bookings = bookingService.getBookingsOwner("ALL", owner.getId());

        assertEquals(2, bookings.size());
    }

    @Test
    void testGetLastBooking() {
        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().minusHours(2));
        booking.setEnd(LocalDateTime.now().minusHours(2));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.APPROVED);

        entityManager.persist(booking);

        BookingShortDto lastBooking = bookingService.getLastBooking(item.getId());

        assertNotNull(lastBooking);
        assertEquals(booking.getId(), lastBooking.getId());
    }

    @Test
    void testGetNextBooking() {
        Booking booking1 = new Booking();
        booking1.setStart(LocalDateTime.now().plusHours(1));
        booking1.setEnd(LocalDateTime.now().plusHours(2));
        booking1.setItem(item);
        booking1.setBooker(booker);
        booking1.setStatus(BookingStatus.APPROVED);
        entityManager.persist(booking1);

        BookingShortDto nextBooking = bookingService.getNextBooking(item.getId());

        assertNotNull(nextBooking);
        assertEquals(booking1.getId(), nextBooking.getId());
    }

    @Test
    void testCreateBookingWithNonExistentUser() {
        BookingInputDto bookingInputDto = new BookingInputDto(item.getId(), LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2));
        Long nonExistentUserId = 999L;

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            bookingService.create(bookingInputDto, nonExistentUserId);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Пользователь с id = " + nonExistentUserId + " не найден", exception.getReason());
    }

    @Test
    void testCreateBookingWithUnavailableItem() {
        item.setAvailable(false);
        entityManager.persist(item);

        BookingInputDto bookingInputDto = new BookingInputDto(item.getId(), LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2));

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            bookingService.create(bookingInputDto, booker.getId());
        });

        assertEquals("Вещь с ID=" + item.getId() + " недоступна для бронирования!", exception.getMessage());
    }

    @Test
    void testCreateBookingByOwner() {
        BookingInputDto bookingInputDto = new BookingInputDto(item.getId(), LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2));

        BookingNotFoundException exception = assertThrows(BookingNotFoundException.class, () -> {
            bookingService.create(bookingInputDto, owner.getId());
        });

        assertEquals("Вещь с ID=" + item.getId() + " недоступна для бронирования самим владельцем!", exception.getMessage());
    }

    @Test
    void testUpdateBookingWithExpiredTime() {
        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().minusHours(2));
        booking.setEnd(LocalDateTime.now().minusHours(1));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);
        entityManager.persist(booking);

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            bookingService.update(booking.getId(), owner.getId(), true);
        });

        assertEquals("Время бронирования уже истекло!", exception.getMessage());
    }

    @Test
    void testUpdateBookingWithNonExistentUser() {
        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().plusHours(1));
        booking.setEnd(LocalDateTime.now().plusHours(2));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);
        entityManager.persist(booking);

        Long nonExistentUserId = 999L;

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            bookingService.update(booking.getId(), nonExistentUserId, true);
        });

        assertEquals("Подтвердить бронирование может только владелец вещи!", exception.getMessage());
    }

    @Test
    void testGetBookingByIdWithNonExistentUser() {
        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().plusHours(1));
        booking.setEnd(LocalDateTime.now().plusHours(2));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);
        entityManager.persist(booking);

        Long nonExistentUserId = 999L;

        BookingNotFoundException exception = assertThrows(BookingNotFoundException.class, () -> {
            bookingService.getBookingById(booking.getId(), nonExistentUserId);
        });

        assertEquals("Посмотреть данные бронирования может только владелец вещи или бронирующий ее!", exception.getMessage());
    }

    @Test
    void testGetBookingsWithUnknownState() {
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            bookingService.getBookings("UNKNOWN", booker.getId());
        });

        assertEquals("Unknown state: UNKNOWN", exception.getMessage());
    }

    @Test
    void testGetBookingsOwnerWithUnknownState() {
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            bookingService.getBookingsOwner("UNKNOWN", owner.getId());
        });

        assertEquals("Unknown state: UNKNOWN", exception.getMessage());
    }

    @Test
    void testGetBookingsAll() {
        Booking booking1 = new Booking();
        booking1.setStart(LocalDateTime.now().plusHours(1));
        booking1.setEnd(LocalDateTime.now().plusHours(2));
        booking1.setItem(item);
        booking1.setBooker(booker);
        booking1.setStatus(BookingStatus.WAITING);

        Booking booking2 = new Booking();
        booking2.setStart(LocalDateTime.now().plusHours(3));
        booking2.setEnd(LocalDateTime.now().plusHours(4));
        booking2.setItem(item);
        booking2.setBooker(booker);
        booking2.setStatus(BookingStatus.WAITING);

        entityManager.persist(booking1);
        entityManager.persist(booking2);

        List<BookingDto> bookings = bookingService.getBookings("ALL", booker.getId());

        assertEquals(2, bookings.size());
    }

    @Test
    void testGetBookingsCurrent() {
        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().minusHours(1));
        booking.setEnd(LocalDateTime.now().plusHours(1));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);

        entityManager.persist(booking);

        List<BookingDto> bookings = bookingService.getBookings("CURRENT", booker.getId());

        assertEquals(1, bookings.size());
    }

    @Test
    void testGetBookingsPast() {
        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().minusHours(2));
        booking.setEnd(LocalDateTime.now().minusHours(1));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);

        entityManager.persist(booking);

        List<BookingDto> bookings = bookingService.getBookings("PAST", booker.getId());

        assertEquals(1, bookings.size());
    }

    @Test
    void testGetBookingsFuture() {
        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().plusHours(1));
        booking.setEnd(LocalDateTime.now().plusHours(2));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);

        entityManager.persist(booking);

        List<BookingDto> bookings = bookingService.getBookings("FUTURE", booker.getId());

        assertEquals(1, bookings.size());
    }

    @Test
    void testGetBookingsWaiting() {
        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().plusHours(1));
        booking.setEnd(LocalDateTime.now().plusHours(2));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);

        entityManager.persist(booking);

        List<BookingDto> bookings = bookingService.getBookings("WAITING", booker.getId());

        assertEquals(1, bookings.size());
    }

    @Test
    void testGetBookingsRejected() {
        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().plusHours(1));
        booking.setEnd(LocalDateTime.now().plusHours(2));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.REJECTED);

        entityManager.persist(booking);

        List<BookingDto> bookings = bookingService.getBookings("REJECTED", booker.getId());

        assertEquals(1, bookings.size());
    }

    @Test
    void testGetBookingsUnknownState() {
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            bookingService.getBookings("UNKNOWN", booker.getId());
        });

        assertEquals("Unknown state: UNKNOWN", exception.getMessage());
    }

    @Test
    void testGetBookingsOwnerAll() {
        Booking booking1 = new Booking();
        booking1.setStart(LocalDateTime.now().plusHours(1));
        booking1.setEnd(LocalDateTime.now().plusHours(2));
        booking1.setItem(item);
        booking1.setBooker(booker);
        booking1.setStatus(BookingStatus.WAITING);

        Booking booking2 = new Booking();
        booking2.setStart(LocalDateTime.now().plusHours(3));
        booking2.setEnd(LocalDateTime.now().plusHours(4));
        booking2.setItem(item);
        booking2.setBooker(booker);
        booking2.setStatus(BookingStatus.WAITING);

        entityManager.persist(booking1);
        entityManager.persist(booking2);

        List<BookingDto> bookings = bookingService.getBookingsOwner("ALL", owner.getId());

        assertEquals(2, bookings.size());
    }

    @Test
    void testGetBookingsOwnerCurrent() {
        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().minusHours(1));
        booking.setEnd(LocalDateTime.now().plusHours(1));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);

        entityManager.persist(booking);

        List<BookingDto> bookings = bookingService.getBookingsOwner("CURRENT", owner.getId());

        assertEquals(1, bookings.size());
    }

    @Test
    void testGetBookingsOwnerPast() {
        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().minusHours(2));
        booking.setEnd(LocalDateTime.now().minusHours(1));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);

        entityManager.persist(booking);

        List<BookingDto> bookings = bookingService.getBookingsOwner("PAST", owner.getId());

        assertEquals(1, bookings.size());
    }

    @Test
    void testGetBookingsOwnerFuture() {
        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().plusHours(1));
        booking.setEnd(LocalDateTime.now().plusHours(2));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);

        entityManager.persist(booking);

        List<BookingDto> bookings = bookingService.getBookingsOwner("FUTURE", owner.getId());

        assertEquals(1, bookings.size());
    }

    @Test
    void testGetBookingsOwnerWaiting() {
        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().plusHours(1));
        booking.setEnd(LocalDateTime.now().plusHours(2));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);

        entityManager.persist(booking);

        List<BookingDto> bookings = bookingService.getBookingsOwner("WAITING", owner.getId());

        assertEquals(1, bookings.size());
    }

    @Test
    void testGetBookingsOwnerRejected() {
        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().plusHours(1));
        booking.setEnd(LocalDateTime.now().plusHours(2));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.REJECTED);

        entityManager.persist(booking);

        List<BookingDto> bookings = bookingService.getBookingsOwner("REJECTED", owner.getId());

        assertEquals(1, bookings.size());
    }

    @Test
    void testGetBookingsOwnerUnknownState() {
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            bookingService.getBookingsOwner("UNKNOWN", owner.getId());
        });

        assertEquals("Unknown state: UNKNOWN", exception.getMessage());
    }

    @Test
    void testGetBookingWithUserBookedItem() {
        // Создание бронирования, которое должно быть найдено
        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().minusHours(2));
        booking.setEnd(LocalDateTime.now().minusHours(1));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.APPROVED);
        entityManager.persist(booking);

        // Вызов метода и проверка результата
        Booking result = bookingService.getBookingWithUserBookedItem(item.getId(), booker.getId());

        assertNotNull(result);
        assertEquals(booking.getId(), result.getId());
        assertEquals(booking.getStart(), result.getStart());
        assertEquals(booking.getEnd(), result.getEnd());
        assertEquals(booking.getStatus(), result.getStatus());
    }

    @Test
    void testGetBookingWithUserBookedItemNotFound() {
        // Вызов метода и проверка, что бронирование не найдено
        Booking result = bookingService.getBookingWithUserBookedItem(item.getId(), booker.getId());

        assertNull(result);
    }



}
