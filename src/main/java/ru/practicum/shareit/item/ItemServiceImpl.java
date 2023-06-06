package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.interfaces.BookingService;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.exceptions.UserNotFoundException;


import ru.practicum.shareit.item.interfaces.ItemRepository;
import ru.practicum.shareit.item.interfaces.CommentRepository;

import ru.practicum.shareit.item.interfaces.ItemService;


import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.interfaces.UserRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.user.model.User;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;

import java.util.List;


import static java.util.stream.Collectors.toList;


@Service

public class ItemServiceImpl implements ItemService {

    @Autowired
    private ItemRepository itemRepository;


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemMapper itemMapper;

    @Autowired
    @Lazy
    private BookingService bookingService;

    @Autowired
    @Lazy
    private CommentRepository commentRepository;


    @Override
    public ItemDto add(ItemDto itemDto, Long userId) {
        if (itemDto.getName() == null || itemDto.getName().isEmpty() || itemDto.getDescription() == null
                || itemDto.getAvailable() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        Item item = itemMapper.fromItemDto(itemDto, user);

        return itemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto update(ItemDto itemDto, Long userId, Long itemId) {


        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException("Item not found!"));

        if (!item.getOwner().getId().equals(userId)) {
            throw new UserNotFoundException("Only owner can change item!");

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


        return itemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto getItemById(Long itemId, Long userId) {
        ItemDto itemDto;
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException("Item not found"));

        if (userId.equals(item.getOwner().getId())) {
            itemDto = itemMapper.toItemDtoWithBookingToOwner(item);
        } else {
            itemDto = itemMapper.toItemDto(item);
        }
        return itemDto;
    }

    @Override
    public Collection<ItemDto> getItemsByOwnerId(Long ownerId) {
        return itemRepository.getItemsByOwnerId(ownerId)
                .stream()
                .map(itemMapper::toItemDtoWithBookingToOwner)
                .sorted(Comparator.comparing(ItemDto::getId)).collect(toList());
    }

    @Override
    public Collection<ItemDto> search(String text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        return itemRepository.search(text)
                .stream()
                .map(itemMapper::toItemDto).collect(toList());
    }

    @Override
    public CommentDto createComment(Long itemId, Long userId, CommentDto commentDto) {
        Comment comment = new Comment();
        Booking booking = bookingService.getItemWithBooker(itemId, userId);

        if (booking != null) {
            comment.setItem(booking.getItem());
            comment.setText(commentDto.getText());
            comment.setAuthor(booking.getBooker());
            comment.setCreated(LocalDateTime.now());
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Booking not found");
        }

        return itemMapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    public Item get(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException("Not found"));
    }

    @Override
    public List<CommentDto> getCommentsByItemId(Long itemId) {
        return commentRepository.findAllByItemId(itemId)
                .stream()
                .map(itemMapper::toCommentDto).collect(toList());
    }


}