package ru.practicum.shareit.booking.interfaces;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.DateBookingDto;
import ru.practicum.shareit.booking.dto.ShortBookingDto;

import java.util.Collection;
import java.util.List;


public interface BookingService {

    // Добавление нового запроса
    BookingDto create(DateBookingDto dateBookingDto, Long bookerId);

    // Подтверждение или отклонение щапроса бронирования
    BookingDto toAccept(Long bookingId, Long ownerId, boolean approve);

    // Получение данных о конкретном бронировании
    BookingDto findBookingByOwnerIdOrBookerId(Long userId, Long bookerId);

    // Получение списка всех бронирований текущего пользователя(то что забронировал сам пользователь)
    List<BookingDto> getBookingsByBookerId(Long bookerId, String state, int size, int from);

    // Получение списка бронирования для всех вещей текущего пользователя(то что забронировали у пользователя)
    List<BookingDto> getBookingsByOwnerId(Long ownerID, String state, int size, int from);

    ShortBookingDto getNextBooking(Long itemId);

    ShortBookingDto getLastBooking(Long itemId);

    Booking getItemWithBooker(Long itemId, Long userID);
}
