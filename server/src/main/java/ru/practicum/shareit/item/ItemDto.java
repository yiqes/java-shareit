package ru.practicum.shareit.item;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.user.User;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemDto {

    Long id;

    String name;

    String description;

    Boolean available;

    @JsonIgnore
    User owner;
    Long requestId;
    BookingShortDto lastBooking;
    BookingShortDto nextBooking;
    List<CommentDto> comments;
}