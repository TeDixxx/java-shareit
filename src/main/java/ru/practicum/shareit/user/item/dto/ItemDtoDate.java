package ru.practicum.shareit.user.item.dto;

import lombok.Data;
import ru.practicum.shareit.booking.Booking;

import java.util.List;

@Data
public class ItemDtoDate {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long ownerId;
    private Booking lastBooking;
    private Booking nextBooking;
    private List<CommentDto> comments;
}
