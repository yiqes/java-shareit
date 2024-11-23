package ru.practicum.shareit.item;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingServiceImpl;
import ru.practicum.shareit.booking.BookingStatus;
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
@Import({ItemMapper.class, ItemServiceImpl.class, ValidationService.class, UserServiceImpl.class, UserMapper.class,
        BookingServiceImpl.class, BookingMapper.class})
public class ItemServiceImplIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemServiceImpl itemService;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        // Создание пользователей
        user1 = new User();
        user1.setEmail("owner@example.com");
        user1.setName("Owner");
        user1.setRegistrationDate(Instant.now());
        entityManager.persist(user1);

        user2 = new User();
        user2.setEmail("booker@example.com");
        user2.setName("Booker");
        user2.setRegistrationDate(Instant.now());
        entityManager.persist(user2);
    }

    @AfterEach
    void tearDown() {
        entityManager.clear();
    }

    @Test
    void testGetItemById() {

        Item item = new Item();
        item.setOwner(user1);
        item.setName("Item Name");
        item.setDescription("Item Description");
        item.setAvailable(true);
        item.setRequestId(null);
        entityManager.persist(item);

        ItemDto itemDto = itemService.getItemById(item.getId(), user1.getId());

        assertNotNull(itemDto);
        assertEquals("Item Name", itemDto.getName());
        assertEquals("Item Description", itemDto.getDescription());
    }

    @Test
    void testCreateItem() {

        ItemDto itemDto = new ItemDto();
        itemDto.setOwner(user1);
        itemDto.setName("Item Name");
        itemDto.setDescription("Item Description");
        itemDto.setAvailable(true);
        itemDto.setRequestId(null);

        ItemDto createdItemDto = itemService.create(itemDto, user1.getId());

        assertNotNull(createdItemDto.getId());
        assertEquals("Item Name", createdItemDto.getName());
        assertEquals("Item Description", createdItemDto.getDescription());
    }

    @Test
    void testGetItemsByOwner() {

        Item item1 = new Item();
        item1.setOwner(user1);
        item1.setName("Item 1");
        item1.setDescription("Description 1");
        item1.setAvailable(true);
        item1.setRequestId(null);

        Item item2 = new Item();
        item2.setOwner(user1);
        item2.setName("Item 2");
        item2.setDescription("Description 2");
        item2.setAvailable(true);
        item2.setRequestId(null);

        entityManager.persist(item1);
        entityManager.persist(item2);

        List<ItemDto> items = itemService.getItemsByOwner(user1.getId());

        assertEquals(2, items.size());
    }

    @Test
    void testDeleteItem() {

        Item item = new Item();
        item.setOwner(user1);
        item.setName("Item Name");
        item.setDescription("Item Description");
        item.setAvailable(true);
        item.setRequestId(null);

        entityManager.persist(item);

        itemService.delete(item.getId(), user1.getId());

        assertFalse(itemRepository.existsById(item.getId()));
    }

    @Test
    void testGetItemsBySearchQuery() {
        Item item1 = new Item();
        item1.setOwner(user1);
        item1.setName("Item 1");
        item1.setDescription("Description 1");
        item1.setAvailable(true);
        item1.setRequestId(null);

        Item item2 = new Item();
        item2.setOwner(user1);
        item2.setName("Item 2");
        item2.setDescription("Description 2");
        item2.setAvailable(true);
        item2.setRequestId(null);

        entityManager.persist(item1);
        entityManager.persist(item2);

        List<ItemDto> items = itemService.getItemsBySearchQuery("Item");

        assertEquals(2, items.size());
    }

    @Test
    void testUpdateItem() {

        Item item = new Item();
        item.setOwner(user1);
        item.setName("Item Name");
        item.setDescription("Item Description");
        item.setAvailable(true);
        item.setRequestId(null);

        entityManager.persist(item);

        ItemDto updateDto = new ItemDto(item.getId(), "Updated Name", "Updated Description", false, null, null, null, null, null);
        ItemDto updatedItemDto = itemService.update(updateDto, user1.getId(), item.getId());

        assertEquals("Updated Name", updatedItemDto.getName());
        assertEquals("Updated Description", updatedItemDto.getDescription());
        assertFalse(updatedItemDto.getAvailable());
    }


    @Test
    void testGetCommentsByItemId() {


        Item item = new Item();
        item.setOwner(user1);
        item.setName("Item Name");
        item.setDescription("Item Description");
        item.setAvailable(true);
        item.setRequestId(null);

        entityManager.persist(item);

        Booking booking = new Booking(null, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1), item, user2, BookingStatus.APPROVED);
        entityManager.persist(booking);

        Comment comment1 = new Comment(null, "Comment 1", item, user2, LocalDateTime.now());
        Comment comment2 = new Comment(null, "Comment 2", item, user2, LocalDateTime.now().plusDays(1));
        entityManager.persist(comment1);
        entityManager.persist(comment2);

        List<CommentDto> comments = itemService.getCommentsByItemId(item.getId());

        assertEquals(2, comments.size());
    }

    @Test
    void testGetByRequestId() {

        Item item1 = new Item();
        item1.setOwner(user1);
        item1.setName("Item 1");
        item1.setDescription("Description 1");
        item1.setAvailable(true);
        item1.setRequestId(1L);

        Item item2 = new Item();
        item2.setOwner(user1);
        item2.setName("Item 2");
        item2.setDescription("Description 2");
        item2.setAvailable(true);
        item2.setRequestId(null);

        entityManager.persist(item1);
        entityManager.persist(item2);

        List<ItemDto> items = itemService.getByRequestId(1L);

        assertEquals(1, items.size());
    }
}
