package ru.practicum.shareit.item.interfaces;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.List;

public interface ItemService {

    ItemDto add(ItemDto itemDto, Long userId);

    ItemDto update(ItemDto itemDto, Long userId, Long itemId);

    ItemDto getItemById(Long itemId, Long userId);

    Collection<ItemDto> getItemsByOwnerId(Long userId);

    Collection<ItemDto> search(String text);

    CommentDto createComment(Long itemID, Long userId, CommentDto commentDto);

    Item get(Long itemId);

    List<CommentDto> getCommentsByItemId(Long itemId);
}
