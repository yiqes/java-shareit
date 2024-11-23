package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@Rollback
@DataJpaTest
@ActiveProfiles("test")
@Import({UserServiceImpl.class, UserMapper.class})
public class UserServiceImplIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        // userService уже инъектирован через @Autowired
    }

    @Test
    void testSaveUser() {
        UserDto userDto = new UserDto(null, "test@example.com", "Test User", Instant.now());
        UserDto savedUserDto = userService.saveUser(userDto);

        assertNotNull(savedUserDto.getId());
        assertEquals("test@example.com", savedUserDto.getEmail());
        assertEquals("Test User", savedUserDto.getName());
    }

    @Test
    void testGetUser() {
        User user = new User(null, "test@example.com", "Test User", Instant.now());
        entityManager.persist(user);

        UserDto userDto = userService.getUser(user.getId());

        assertNotNull(userDto);
        assertEquals("test@example.com", userDto.getEmail());
        assertEquals("Test User", userDto.getName());
    }

    @Test
    void testGetAllUsers() {
        User user1 = new User(null, "test1@example.com", "Test User 1", Instant.now());
        User user2 = new User(null, "test2@example.com", "Test User 2", Instant.now());
        entityManager.persist(user1);
        entityManager.persist(user2);

        List<UserDto> users = userService.getAllUsers();

        assertEquals(2, users.size());
        assertTrue(users.stream().anyMatch(u -> u.getEmail().equals("test1@example.com")));
        assertTrue(users.stream().anyMatch(u -> u.getEmail().equals("test2@example.com")));
    }

    @Test
    void testUpdateUser() {
        User user = new User(null, "test@example.com", "Test User", Instant.now());
        entityManager.persist(user);

        UserDto updatedUserDto = new UserDto(user.getId(), "updated@example.com", "Updated User", Instant.now());
        UserDto savedUserDto = userService.updateUserById(user.getId(), updatedUserDto);

        assertNotNull(savedUserDto);
        assertEquals("updated@example.com", savedUserDto.getEmail());
        assertEquals("Updated User", savedUserDto.getName());
    }

    @Test
    void testDeleteUser() {
        User user = new User(null, "test@example.com", "Test User", Instant.now());
        entityManager.persist(user);

        userService.deleteUser(user.getId());

        assertFalse(userRepository.findById(user.getId()).isPresent());
    }
}
