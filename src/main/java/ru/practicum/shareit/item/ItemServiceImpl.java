package ru.practicum.shareit.item;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;

    public ItemServiceImpl(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Override
    public Item addItem(Item item) {
        return itemRepository.save(item);
    }

    private Long getNextId() {
        return itemRepository.findAll().stream()
                .mapToLong(Item::getId)
                .max()
                .orElse(0) + 1;
    }

    @Override
    public Item updateItem(Long itemId, Item item) {
        return itemRepository.updateItem(item, itemId);
    }

    @Override
    public Item getItem(Long itemId) {
        return itemRepository.findById(itemId);
    }

    @Override
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }
}