package ru.practicum.shareit.item.interfaces;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface ItemStorage {

    Item add(Item item, User user);

    Item update(Item item);

    Item getItemById(Long itemId);

    Collection<Item> getOwnerItems(Long userId);

    Collection<Item> search(String text);
}
