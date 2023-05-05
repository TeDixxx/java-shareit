package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.interfaces.ItemService;
import ru.practicum.shareit.item.interfaces.ItemStorage;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.Collection;


@Service
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final UserServiceImpl userService;

    public ItemServiceImpl(@Qualifier("inMemoryItemStorage") ItemStorage itemStorage, UserServiceImpl userService) {
        this.itemStorage = itemStorage;
        this.userService = userService;
    }

    @Override
    public ItemDto add(ItemDto itemDto, Long userId) throws ValidationException, UserNotFoundException {
        UserDto userDto = userService.getUserById(userId);

        User user = UserMapper.fromUserDto(userDto);
        Item item = itemStorage.add(ItemMapper.fromItemDto(itemDto, user), user);

        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto update(ItemDto itemDto, Long userId, Long itemId) throws ValidationException {
        User user = UserMapper.fromUserDto(userService.getUserById(userId));
        Item item = itemStorage.getItemById(itemId);

        if (!item.getOwner().getId().equals(userId)) {
            throw new UserNotFoundException("Only owner can change item!");

        }

        if (itemDto.getName() == null) {
            itemDto.setName(item.getName());
        }

        if (itemDto.getDescription() == null) {
            itemDto.setDescription(item.getDescription());
        }

        if (itemDto.getAvailable() == null) {
            itemDto.setAvailable(item.getAvailable());
        }

        itemDto.setId(itemId);

        itemStorage.update(ItemMapper.fromItemDto(itemDto, user));

        return itemDto;
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        return ItemMapper.toItemDto(itemStorage.getItemById(itemId));
    }

    @Override
    public Collection<ItemDto> getOwnerItems(Long userId) {
        Collection<Item> items = itemStorage.getOwnerItems(userId);
        Collection<ItemDto> itemsDto = new ArrayList<>();

        for (Item item : items) {
            itemsDto.add(ItemMapper.toItemDto(item));
        }
        return itemsDto;
    }

    @Override
    public Collection<ItemDto> search(String text) throws ValidationException {
        Collection<Item> items = itemStorage.search(text);
        Collection<ItemDto> itemsDto = new ArrayList<>();

        for (Item item : items) {
            itemsDto.add(ItemMapper.toItemDto(item));

        }
        return itemsDto;
    }

}
