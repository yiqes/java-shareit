package ru.practicum.shareit.request;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.booking.BookingServiceImpl;
import ru.practicum.shareit.exception.RequestNotFoundException;
import ru.practicum.shareit.item.ItemServiceImpl;
import ru.practicum.shareit.service.ValidationService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserServiceImpl;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import({RequestServiceImpl.class, ValidationService.class, RequestMapper.class, UserServiceImpl.class,
        UserMapper.class, ItemServiceImpl.class, BookingServiceImpl.class})
@ActiveProfiles("test")
class RequestServiceImplIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private RequestServiceImpl requestService;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        // Создание пользователей
        user1 = new User();
        user1.setEmail("test1@example.com");
        user1.setName("Test User 1");
        user1.setRegistrationDate(Instant.now());
        entityManager.persist(user1);

        user2 = new User();
        user2.setEmail("test2@example.com");
        user2.setName("Test User 2");
        user2.setRegistrationDate(Instant.now());
        entityManager.persist(user2);

        // Создание запросов
        Request request1 = new Request();
        request1.setDescription("Test description 1");
        request1.setRequestor(user1);
        request1.setCreated(LocalDateTime.now());
        entityManager.persist(request1);

        Request request2 = new Request();
        request2.setDescription("Test description 2");
        request2.setRequestor(user2);
        request2.setCreated(LocalDateTime.now());
        entityManager.persist(request2);
    }

    @AfterEach
    void tearDown() {
        // Очистка данных после каждого теста
        requestRepository.deleteAll();
        entityManager.clear();
    }

    @Test
    void testSaveRequest() {
        RequestDto requestDto = new RequestDto();
        requestDto.setDescription("Test description");
        Long requestorId = user1.getId();
        LocalDateTime created = LocalDateTime.now();

        RequestDto savedRequestDto = requestService.saveRequest(requestDto, requestorId, created);

        assertNotNull(savedRequestDto.getId());
        assertEquals("Test description", savedRequestDto.getDescription());
    }

    @Test
    void testGetRequestById() {
        Request request = new Request();
        request.setDescription("Test description");
        request.setRequestor(user1);
        request.setCreated(LocalDateTime.now());
        entityManager.persist(request);

        RequestDto requestDto = requestService.getRequestById(request.getId(), user1.getId());

        assertNotNull(requestDto);
        assertEquals("Test description", requestDto.getDescription());
    }

    @Test
    void testGetRequestByIdRequestNotFound() {
        Long nonExistentRequestId = 999L;

        assertThrows(RequestNotFoundException.class, () -> requestService.getRequestById(nonExistentRequestId, user1.getId()));
    }

    @Test
    void testGetOwnRequests() {
        List<RequestDto> requests = requestService.getOwnRequests(user1.getId());

        assertEquals(1, requests.size());
        assertTrue(requests.stream().anyMatch(r -> r.getDescription().equals("Test description 1")));
    }

    @Test
    void testGetAllRequestsWithoutSize() {
        Integer from = 0;
        Integer size = null;

        List<RequestDto> requests = requestService.getAllRequests(user1.getId(), from, size);

        assertEquals(1, requests.size());
        assertTrue(requests.stream().anyMatch(r -> r.getDescription().equals("Test description 2")));
    }

    @Test
    void testGetAllRequests_WithSize() {
        Integer from = 0;
        Integer size = 2;

        List<RequestDto> requests = requestService.getAllRequests(user1.getId(), from, size);

        assertEquals(1, requests.size());
        assertTrue(requests.stream().anyMatch(r -> r.getDescription().equals("Test description 2")));
    }
}
