package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.UserNotFoundException;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private static final String USER_ID = "{user-id}";

    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestBody UserDto userDto) {
        UserDto createdUser = userService.saveUser(userDto);
        return ResponseEntity.ok(createdUser);
    }

    @GetMapping(USER_ID)
    public ResponseEntity<UserDto> getUser(@PathVariable("user-id") Long userId) {
        UserDto userDto = userService.getUser(userId);
        return userDto != null ? ResponseEntity.ok(userDto) : ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> users = userService.getAllUsers();
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(USER_ID)
    public ResponseEntity<UserDto> updateUser(@PathVariable("user-id") Long userId, @RequestBody UserDto userDto) {
        try {
            UserDto updatedUser = userService.updateUserById(userId, userDto);
            return ResponseEntity.ok(updatedUser);
        } catch (UserNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping(USER_ID)
    public ResponseEntity<Void> deleteUser(@PathVariable("user-id") Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}