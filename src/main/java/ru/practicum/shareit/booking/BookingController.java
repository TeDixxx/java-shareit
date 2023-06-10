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
        return bookingService.create(dateBookingDto,bookerId);
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
    public Collection<BookingDto> getBookingsByBookerId(@RequestHeader("X-Sharer-User-Id") Long bookerId,
                                                        @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getBookingsByBookerId(bookerId, state);
    }

    @GetMapping("/owner")
    public Collection<BookingDto> getBookingsByOwnerId(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                       @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getBookingsByOwnerId(ownerId, state);
    }

}
