package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.DateBookingDto;
import ru.practicum.shareit.booking.interfaces.BookingService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    @Autowired
    @Lazy
    private final BookingService bookingService;

    @PostMapping
    public BookingDto create(@RequestBody @Valid DateBookingDto dateBookingDto,
                             @RequestHeader("X-Sharer-User-Id") Long bookerId) {
        return bookingService.create(dateBookingDto, bookerId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto toAccept(@PathVariable Long bookingId, @RequestHeader("X-Sharer-User-Id") Long userId,
                               @RequestParam boolean approved) {
        return bookingService.toAccept(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingByOwnerOrBookerId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                  @PathVariable Long bookingId) {
        return bookingService.findBookingByOwnerIdOrBookerId(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getBookingsByBookerId(@RequestHeader("X-Sharer-User-Id") Long bookerId,
                                                  @RequestParam(defaultValue = "ALL") String state,
                                                  @RequestParam(defaultValue = "0") int from,
                                                  @RequestParam(defaultValue = "10") int size) {
        return bookingService.getBookingsByBookerId(bookerId, state, size, from);
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingsByOwnerId(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                       @RequestParam(defaultValue = "ALL") String state,
                                                       @RequestParam(defaultValue = "0") int from,
                                                       @RequestParam(defaultValue = "10") int size) {
        return bookingService.getBookingsByOwnerId(ownerId, state, size, from);
    }

}
