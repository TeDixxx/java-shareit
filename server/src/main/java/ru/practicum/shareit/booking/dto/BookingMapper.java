package ru.practicum.shareit.booking.dto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.interfaces.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.interfaces.UserService;
import ru.practicum.shareit.user.model.User;


@Component
public class BookingMapper {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    @Autowired
    @Lazy
    private ItemMapper itemMapper;

    public static BookingDto toBookingDto(Booking booking) {
        if (booking != null) {
            return new BookingDto(booking.getId(),
                    booking.getStart(),
                    booking.getEnd(),
                    booking.getItem(),
                    UserMapper.toUserDto(booking.getBooker()),
                    booking.getStatus());
        } else {
            return null;
        }
    }


    public static ShortBookingDto toShortBookingDto(Booking booking) {
        if (booking != null) {
            return new ShortBookingDto(booking.getId(),
                    booking.getBooker().getId(),
                    booking.getStart(),
                    booking.getEnd());
        } else {
            return null;
        }

    }

    public static Booking toBooking(DateBookingDto dateBookingDto, Item item, User user) {
        return new Booking(null,
                dateBookingDto.getStart(),
                dateBookingDto.getEnd(),
                item,
                user,
                Status.WAITING);
    }
}
