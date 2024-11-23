package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.service.ValidationService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ItemServiceImpl implements ItemService {
    ItemRepository repository;
    CommentRepository commentRepository;
    ValidationService validationService;
    ItemMapper mapper;

    @Autowired
    @Lazy
    public ItemServiceImpl(ItemRepository repository, CommentRepository commentRepository,
                           ValidationService validationService, ItemMapper itemMapper) {
        this.repository = repository;
        this.commentRepository = commentRepository;
        this.validationService = validationService;
        this.mapper = itemMapper;
    }

    @Override
    public ItemDto getItemById(Long id, Long userId) {
        ItemDto itemDto;
        Item item = repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Вещь с ID=" + id + " не найдена!"));
        if (userId.equals(item.getOwner().getId())) {
            itemDto = mapper.toItemExtDto(item);
        } else {
            itemDto = mapper.toItemDto(item);
        }
        return itemDto;
    }

    @Override
    public Item findItemById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Вещь с ID=" + id + " не найдена!"));
    }

    @Override
    public ItemDto create(ItemDto itemDto, Long ownerId) {
        validationService.isExistUser(ownerId);
        if (itemDto.getAvailable() == null || itemDto.getName() == null || itemDto.getName().isBlank() ||
        itemDto.getDescription() == null || itemDto.getDescription().isBlank()) {
            throw new ValidationException("Невенрные данные для предмета, проверьте название, описание и статус");
        }
        return mapper.toItemDto(repository.save(mapper.toItem(itemDto, ownerId)));
    }

    @Override
    public List<ItemDto> getItemsByOwner(Long ownerId) {
        validationService.isExistUser(ownerId);
        return repository.findByOwnerId(ownerId).stream()
                .map(mapper::toItemExtDto)
                .sorted(Comparator.comparing(ItemDto::getId))
                .collect(toList());
    }

    @Override
    public void delete(Long itemId, Long ownerId) {
        try {
            Item item = repository.findById(itemId)
                    .orElseThrow(() -> new UserNotFoundException("Вещь с ID=" + itemId + " не найдена!"));
            if (!item.getOwner().getId().equals(ownerId)) {
                throw new ItemNotFoundException("У пользователя нет такой вещи!");
            }
            repository.deleteById(itemId);
        } catch (EmptyResultDataAccessException e) {
            throw new ItemNotFoundException("Вещь с ID=" + itemId + " не найдена!");
        }
    }

    @Override
    public List<ItemDto> getItemsBySearchQuery(String text) {
        if ((text != null) && (!text.isEmpty()) && (!text.isBlank())) {
            text = text.toLowerCase();
            return repository.getItemsBySearchQuery(text).stream()
                    .filter(item -> item.getAvailable() != null && item.getAvailable())
                    .map(mapper::toItemDto)
                    .collect(toList());
        } else return new ArrayList<>();
    }

    @Override
    public ItemDto update(ItemDto itemDto, Long ownerId, Long itemId) {
        validationService.isExistUser(ownerId);
        Item item = repository.findById(itemId)
                .orElseThrow(() -> new UserNotFoundException("Вещь с ID=" + itemId + " не найдена!"));
        if (!item.getOwner().getId().equals(ownerId)) {
            throw new UserNotFoundException("У пользователя нет такой вещи!");
        }
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        return mapper.toItemDto(repository.save(item));
    }

    @Override
    public CommentDto createComment(CommentDto commentDto, Long itemId, Long userId) {
        validationService.isExistUser(userId);
        Booking booking = validationService.getBookingWithUserBookedItem(itemId, userId);
        Comment comment = new Comment();

        if (booking != null) {
            comment.setCreated(LocalDateTime.now());
            comment.setItem(findItemById(itemId));
            comment.setAuthor(validationService.findUserById(userId));
            comment.setText(commentDto.getText());
            return mapper.toCommentDto(commentRepository.save(comment));
        } else {
            throw new ValidationException("Данный пользователь вещь не бронировал!");
        }
    }

    @Override
    public List<CommentDto> getCommentsByItemId(Long itemId) {
        return commentRepository.findAllByItemId(itemId,
                        Sort.by(Sort.Direction.DESC, "created")).stream()
                .map(mapper::toCommentDto)
                .collect(toList());
    }

    @Override
    public List<ItemDto> getByRequestId(Long requestId) {
        return repository.findAllByRequestId(requestId,
                        Sort.by(Sort.Direction.DESC, "id")).stream()
                .map(mapper::toItemDto)
                .collect(toList());
    }
}