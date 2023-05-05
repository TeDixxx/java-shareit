package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.item.interfaces.ItemStorage;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;


@Slf4j
@Component("inMemoryItemStorage")
public class InMemoryItemStorage implements ItemStorage {

    private final HashMap<Long, Item> items = new HashMap<>();
    private Long currentId = 1L;


    @Override
    public Item add(Item item, User user) {
        item.setId(currentId++);
        item.setOwner(user);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(Item item) {
        return items.put(item.getId(), item);
    }

    @Override
    public Item getItemById(Long itemId) {
        if (items.containsKey(itemId)) {
            return items.get(itemId);
        } else {
            log.warn("Item with id {} not found.", itemId);
            throw new ItemNotFoundException("Item not found. Invalid id");
        }

    }

    @Override
    public Collection<Item> getOwnerItems(Long userId) {
        List<Item> userItems = new ArrayList<>();
        for (Item item : items.values()) {
            if (item.getOwner().getId().equals(userId)) {
                userItems.add(item);
            }
        }
        return userItems;
    }

    @Override
    public Collection<Item> search(String text) {

        Collection<Item> searchedItem = new ArrayList<>();
        text = text.toLowerCase();

        if (text.isEmpty()) {
            return searchedItem;
        }
        for (Item item : items.values()) {
            if (item.getName().toLowerCase().contains(text) || item.getDescription().toLowerCase().contains(text)) {
                if (item.getAvailable()) {
                    searchedItem.add(item);
                }
            }
        }
        return searchedItem;
    }
}
