package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.ShortBookingDto;
import ru.practicum.shareit.request.model.ItemRequest;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
public class ItemDtoDate {
    private Long id;
    @NotEmpty
    @NotNull
    private String name;
    @NotEmpty
    private String description;
    @NotNull
    private Boolean available;
    private Long ownerId;

    private ItemRequest itemRequest;

    private ShortBookingDto lastBooking;
    private ShortBookingDto nextBooking;

    private List<CommentDto> comments;
}
