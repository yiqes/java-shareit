package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.exception.BookingNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.service.ValidationService;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookingServiceImpl implements BookingService {

    UserService userService;

    BookingRepository repository;
    BookingMapper mapper;
    ValidationService validationService;

    @Autowired
    @Lazy
    public BookingServiceImpl(BookingRepository bookingRepository, BookingMapper bookingMapper,
                              ValidationService validationService,
                              UserService userService) {
        this.repository = bookingRepository;
        this.mapper = bookingMapper;
        this.validationService = validationService;
        this.userService = userService;
    }

    @Override
    public BookingDto create(BookingInputDto bookingInputDto, Long bookerId) {
        if (!validationService.isExistUser(bookerId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь с id = " + bookerId + " не найден");
        }
        if (!validationService.isAvailableItem(bookingInputDto.getItemId())) {
            throw new ValidationException("Вещь с ID=" + bookingInputDto.getItemId() +
                    " недоступна для бронирования!");
        }
        if (userService.getUser(bookerId) == null) {
            throw new UserNotFoundException("Пользователь с id = " + bookerId + " не найден");
        }
        Booking booking = mapper.toBooking(bookingInputDto, bookerId);
        if (bookerId.equals(booking.getItem().getOwner().getId())) {
            throw new BookingNotFoundException("Вещь с ID=" + bookingInputDto.getItemId() +
                    " недоступна для бронирования самим владельцем!");
        }
        return mapper.toBookingDto(repository.save(booking));
    }

    @Override
    public BookingDto update(Long bookingId, Long userId, Boolean approved) {
        validationService.isExistUser(userId);
        Booking booking = repository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Бронирование с ID=" + bookingId + " не найдено!"));
        if (booking.getEnd().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Время бронирования уже истекло!");
        }

        if (booking.getBooker().getId().equals(userId)) {
            if (!approved) {
                booking.setStatus(BookingStatus.CANCELED);
                log.info("Пользователь с ID={} отменил бронирование с ID={}", userId, bookingId);
            } else {
                throw new BookingNotFoundException("Подтвердить бронирование может только владелец вещи!");
            }
        } else if ((validationService.isItemOwner(booking.getItem().getId(), userId)) &&
                (!booking.getStatus().equals(BookingStatus.CANCELED))) {
            if (!booking.getStatus().equals(BookingStatus.WAITING)) {
                throw new ValidationException("Решение по бронированию уже принято!");
            }
            if (approved) {
                booking.setStatus(BookingStatus.APPROVED);
                log.info("Пользователь с ID={} подтвердил бронирование с ID={}", userId, bookingId);
            } else {
                booking.setStatus(BookingStatus.REJECTED);
                log.info("Пользователь с ID={} отклонил бронирование с ID={}", userId, bookingId);
            }
        } else {
            if (booking.getStatus().equals(BookingStatus.CANCELED)) {
                throw new ValidationException("Бронирование было отменено!");
            } else {
                throw new ValidationException("Подтвердить бронирование может только владелец вещи!");
            }
        }

        return mapper.toBookingDto(repository.save(booking));
    }

    @Override
    public BookingDto getBookingById(Long bookingId, Long userId) {
        validationService.isExistUser(userId);
        Booking booking = repository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Бронирование с ID=" + bookingId + " не найдено!"));
        if (booking.getBooker().getId().equals(userId) || validationService.isItemOwner(booking.getItem().getId(), userId)) {
            return mapper.toBookingDto(booking);
        } else {
            throw new BookingNotFoundException("Посмотреть данные бронирования может только владелец вещи" +
                    " или бронирующий ее!");
        }
    }

    @Override
    public List<BookingDto> getBookings(String state, Long userId) {
        validationService.isExistUser(userId);
        List<Booking> bookings;
        Sort sortByStartDesc = Sort.by(Sort.Direction.DESC, "start");
        switch (state) {
            case "ALL":
                bookings = repository.findByBookerId(userId, sortByStartDesc);
                break;
            case "CURRENT":
                bookings = repository.findByBookerIdAndStartIsBeforeAndEndIsAfter(userId, LocalDateTime.now(),
                        LocalDateTime.now(), sortByStartDesc);
                break;
            case "PAST":
                bookings = repository.findByBookerIdAndEndIsBefore(userId, LocalDateTime.now(), sortByStartDesc);
                break;
            case "FUTURE":
                bookings = repository.findByBookerIdAndStartIsAfter(userId, LocalDateTime.now(), sortByStartDesc);
                break;
            case "WAITING":
                bookings = repository.findByBookerIdAndStatus(userId, BookingStatus.WAITING, sortByStartDesc);
                break;
            case "REJECTED":
                bookings = repository.findByBookerIdAndStatus(userId, BookingStatus.REJECTED, sortByStartDesc);
                break;
            default:
                throw new ValidationException("Unknown state: " + state);
        }
        return bookings.stream()
                .map(mapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getBookingsOwner(String state, Long userId) {
        validationService.isExistUser(userId);
        List<Booking> bookings;
        Sort sortByStartDesc = Sort.by(Sort.Direction.DESC, "start");
        switch (state) {
            case "ALL":
                if (userService.findUserById(userId) == null) {
                    throw new UserNotFoundException("Пользователь с id = " + userId + " не найден!");
                }
                bookings = repository.findByItemOwnerId(userId, sortByStartDesc);
                break;
            case "CURRENT":
                bookings = repository.findByItemOwnerIdAndStartIsBeforeAndEndIsAfter(userId, LocalDateTime.now(),
                        LocalDateTime.now(), sortByStartDesc);
                break;
            case "PAST":
                bookings = repository.findByItemOwnerIdAndEndIsBefore(userId, LocalDateTime.now(), sortByStartDesc);
                break;
            case "FUTURE":
                bookings = repository.findByItemOwnerIdAndStartIsAfter(userId, LocalDateTime.now(),
                        sortByStartDesc);
                break;
            case "WAITING":
                bookings = repository.findByItemOwnerIdAndStatus(userId, BookingStatus.WAITING, sortByStartDesc);
                break;
            case "REJECTED":
                bookings = repository.findByItemOwnerIdAndStatus(userId, BookingStatus.REJECTED, sortByStartDesc);
                break;
            default:
                throw new ValidationException("Unknown state: " + state);
        }
        return bookings.stream()
                .map(mapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public BookingShortDto getLastBooking(Long itemId) {
        BookingShortDto bookingShortDto =
                mapper.toBookingShortDto(repository.findFirstByItemIdAndEndBeforeOrderByEndDesc(itemId,
                        LocalDateTime.now()));
        return bookingShortDto;
    }

    @Override
    public BookingShortDto getNextBooking(Long itemId) {
        BookingShortDto bookingShortDto =
                mapper.toBookingShortDto(repository.findFirstByItemIdAndStartAfterOrderByStartAsc(itemId,
                        LocalDateTime.now()));
        return bookingShortDto;
    }

    @Override
    public Booking getBookingWithUserBookedItem(Long itemId, Long userId) {
        return repository.findFirstByItemIdAndBookerIdAndEndIsBeforeAndStatus(itemId,
                userId, LocalDateTime.now(), BookingStatus.APPROVED);
    }
}