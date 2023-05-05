package ru.practicum.shareit.item.interfaces;

import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import java.util.Collection;

public interface ItemService {

    ItemDto add(ItemDto itemDto, Long userId) throws ValidationException;

    ItemDto update(ItemDto itemDto, Long userId, Long itemId) throws ValidationException;

    ItemDto getItemById(Long itemId);

    Collection<ItemDto> getOwnerItems(Long userId);

    Collection<ItemDto> search(String text) throws ValidationException;
}
