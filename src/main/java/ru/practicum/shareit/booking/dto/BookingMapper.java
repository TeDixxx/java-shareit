package ru.practicum.shareit.booking.dto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.interfaces.ItemService;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.interfaces.UserService;


@Component
public class BookingMapper {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    @Autowired
    @Lazy
    private ItemMapper itemMapper;

    public BookingDto toBookingDto(Booking booking) {
        if (booking != null) {
            return new BookingDto(booking.getId(),
                    booking.getStart(),
                    booking.getEnd(),
                    itemMapper.toItemDto(booking.getItem()),
                    UserMapper.toUserDto(booking.getBooker()),
                    booking.getStatus());
        } else {
            return null;
        }
    }


    public ShortBookingDto toShortBookingDto(Booking booking) {
        if (booking != null) {
            return new ShortBookingDto(booking.getId(),
                    booking.getBooker().getId(),
                    booking.getStart(),
                    booking.getEnd());
        } else {
            return null;
        }

    }

    public Booking toBooking(InputBookingDto inputBookingDto, Long bookerId) {
        return new Booking(null,
                inputBookingDto.getStart(),
                inputBookingDto.getEnd(),
                itemService.get(inputBookingDto.getItemId()),
                userService.get(bookerId),
                Status.WAITING);
    }
}
