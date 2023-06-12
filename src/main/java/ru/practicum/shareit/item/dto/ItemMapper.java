package ru.practicum.shareit.item.dto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.interfaces.BookingService;
import ru.practicum.shareit.item.interfaces.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
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
                itemService.getCommentsByItemId(item.getId()),
                item.getRequest() == null ? null : item.getRequest().getId());

    }

    public Item fromItemDto(ItemDto itemDto, User owner) {
        return new Item(itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                owner,
                itemDto.getRequestId() == null ? null : ItemRequest.builder().id(itemDto.getRequestId()).build());
    }


    public  ItemDto toItemDtoWithBookingToOwner(Item item) {
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
