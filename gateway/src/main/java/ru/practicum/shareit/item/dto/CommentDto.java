package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentDto {
    Long id;                // уникальный идентификатор комментария;
    @NotBlank
    String text;            // содержимое комментария;
    String authorName;      // имя автора комментария;
    LocalDateTime created;  // дата создания комментария.
}