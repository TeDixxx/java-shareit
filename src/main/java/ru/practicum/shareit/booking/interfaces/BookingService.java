package ru.practicum.shareit.booking.interfaces;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.InputBookingDto;
import ru.practicum.shareit.booking.dto.ShortBookingDto;

import java.util.Collection;


public interface BookingService {

    // Добавление нового запроса
    BookingDto create(InputBookingDto inputBookingDto, Long bookerId);

    // Подтверждение или отклонение щапроса бронирования
    BookingDto toAccept(Long bookingId, Long ownerId, boolean approve);

    // Получение данных о конкретном бронировании
    BookingDto findBookingByOwnerIdOrBookerId(Long userId, Long bookerId);

    // Получение списка всех бронирований текущего пользователя(то что забронировал сам пользователь)
    Collection<BookingDto> getBookingsByBookerId(Long bookerId, String state);

    // Получение списка бронирования для всех вещей текущего пользователя(то что забронировали у пользователя)
    Collection<BookingDto> getBookingsByOwnerId(Long ownerID, String state);

    ShortBookingDto getNextBooking(Long itemId);

    ShortBookingDto getLastBooking(Long itemId);

    Booking getItemWithBooker(Long itemId, Long userID);
}
