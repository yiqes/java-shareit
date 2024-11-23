package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient userClient;
    private static final String PATH = "{user-id}";

    @GetMapping
    public ResponseEntity<Object> getUsers() {
        return userClient.getUsers();
    }

    @GetMapping(PATH)
    public ResponseEntity<Object> getUserById(@PathVariable("user-id") Long userId) {
        return userClient.getUserById(userId);
    }

    @ResponseBody
    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody UserDto userDto) {
        log.info("Получен POST-запрос к эндпоинту: '/users' на добавление пользователя");
        return userClient.create(userDto);
    }

    @ResponseBody
    @PatchMapping(PATH)
    public ResponseEntity<Object> update(@PathVariable("user-id") Long userId, @RequestBody UserDto userDto) {
        log.info("Получен PATCH-запрос к эндпоинту: '/users' на обновление пользователя с ID{}", userId);
        return userClient.update(userDto, userId);
    }

    @DeleteMapping(PATH)
    public ResponseEntity<Object> delete(@PathVariable("user-id") Long userId) {
        log.info("Получен DELETE-запрос к эндпоинту: '/users' на обновление пользователя с ID{}", userId);
        return userClient.delete(userId);
    }
}
