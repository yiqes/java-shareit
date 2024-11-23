package ru.practicum.shareit.user;

import java.util.List;

public interface UserService {

    UserDto saveUser(UserDto userDto);

    UserDto getUser(Long id);

    List<UserDto> getAllUsers();

    void deleteUser(Long id);

    UserDto updateUserById(Long id, UserDto userDto);

    User findUserById(Long userId);

    boolean existsById(Long id);
}