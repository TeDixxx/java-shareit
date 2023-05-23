package ru.practicum.shareit.item.dto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.interfaces.BookingService;
import ru.practicum.shareit.item.interfaces.ItemService;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Component
public class ItemMapper {

    @Autowired
    @Lazy
    private BookingService bookingService;

    @Autowired
    @Lazy
    private ItemService itemService;

    public ItemDto toItemDto(Item item) {
        return new ItemDto(item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner(),
                null,
                null,
                itemService.getCommentsByItemId(item.getId()));

    }

    public Item fromItemDto(ItemDto itemDto, User owner) {
        return new Item(itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                owner);
    }

    public CommentDto toCommentDto(Comment comment) {
        return new CommentDto(comment.getId(),
                comment.getText(),
                comment.getItem(),
                comment.getAuthor().getName(),
                comment.getCreated());
    }

    public ItemDto toItemDtoWithBooking(Item item) {
        return new ItemDto(item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner(),
                bookingService.getLastBooking(item.getId()),
                bookingService.getNextBooking(item.getId()),
                itemService.getCommentsByItemId(item.getId()));
    }


}
