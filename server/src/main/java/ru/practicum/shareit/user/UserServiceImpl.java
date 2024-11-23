package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public UserDto saveUser(UserDto userDto) {
        if (!userDto.getEmail().contains("@")) {
            throw new ValidationException("неверно задан email");
        }
        User user = userMapper.toUser(userDto);
        User savedUser = userRepository.save(user);
        return userMapper.toUserDto(savedUser);
    }

    @Override
    public UserDto getUser(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toUserDto)
                .orElse(null);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public UserDto updateUserById(Long id, UserDto userDto) {
        return userRepository.findById(id)
                .map(existingUser -> {
                    if (userDto.getName() != null) {
                        existingUser.setName(userDto.getName());
                    }
                    if (userDto.getEmail() != null) {
                        existingUser.setEmail(userDto.getEmail());
                    }
                    User updatedUser = userRepository.save(existingUser);

                    return userMapper.toUserDto(updatedUser);
                })
                .orElseThrow(() -> new UserNotFoundException("User not found with id = " + id));
    }

    @Override
    public boolean existsById(Long id) {
        return userRepository.existsById(id);
    }

    @Override
    public User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id = " + userId + " not found!"));
    }
}
