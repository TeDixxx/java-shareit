package ru.practicum.shareit.booking;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.dto.*;
import ru.practicum.shareit.booking.interfaces.BookingRepository;
import ru.practicum.shareit.booking.interfaces.BookingService;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.user.item.interfaces.ItemService;
import ru.practicum.shareit.user.item.model.Item;
import ru.practicum.shareit.user.interfaces.UserService;


import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Comparator;

import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    @Autowired
    private final BookingRepository bookingRepository;

    @Autowired
    private final ItemService itemService;

    @Autowired
    @Lazy
    public final UserService userService;

    @Autowired
    private final BookingMapper bookingMapper;

    @Override
    public BookingDto create(DateBookingDto dateBookingDto, Long bookerId) {

        Item item = itemService.get(dateBookingDto.getItemId());

        if (!item.getAvailable()) {
            throw new UnAvailableException("Item not available");
        }

        if (item.getOwner().getId().equals(bookerId)) {
            throw new UserNotFoundException("owner can't booking his item");
        }

        Booking booking = bookingMapper.toBooking(dateBookingDto, bookerId);

        if (dateBookingDto.getStart().isBefore(LocalDateTime.now())
                || dateBookingDto.getEnd().isBefore(LocalDateTime.now())
                || dateBookingDto.getEnd().isBefore(dateBookingDto.getStart())
                || dateBookingDto.getStart().equals(dateBookingDto.getEnd())) {
            throw new IncorrectDataException("Incorrect time for booking");
        }

        bookingRepository.save(booking);

        return bookingMapper.toBookingDto(booking);
    }

    @Override
    public BookingDto toAccept(Long bookingId, Long userId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(()
                -> new BookingNotFoundException("Booking not found"));

        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Only owner can put approve");
        }

        if (booking.getStatus().equals(Status.APPROVED)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status equals approved");
        }

        if (approved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }


        bookingRepository.save(booking);

        return bookingMapper.toBookingDto(booking);
    }

    @Override
    public BookingDto findBookingByOwnerIdOrBookerId(Long userId, Long bookingId) {
        check(userId);
        Booking booking = bookingRepository.findBookingByOwnerOrBooker(userId, bookingId).orElseThrow(()
                -> new BookingNotFoundException("Booking not found"));
        return bookingMapper.toBookingDto(booking);
    }

    @Override
    public Collection<BookingDto> getBookingsByBookerId(Long bookerId, String state) {
        Collection<Booking> bookings;
        check(bookerId);
        switch (state) {
            case "WAITING":
                bookings = bookingRepository.findByBooker_IdAndStatusOrderByStartDesc(bookerId, Status.WAITING);
                break;

            case "REJECTED":
                bookings = bookingRepository.findByBooker_IdAndStatusOrderByStartDesc(bookerId, Status.REJECTED);
                break;

            case "ALL":
                bookings = bookingRepository.findByBooker_IdOrderByStartDesc(bookerId);
                break;

            case "CURRENT":
                bookings = bookingRepository.findByBooker_IdAndStartBeforeAndEndAfterOrderByStartDesc(bookerId,
                        LocalDateTime.now(), LocalDateTime.now());
                break;

            case "FUTURE":
                bookings = bookingRepository.findByBooker_IdAndStartAfterOrderByStartDesc(bookerId,
                        LocalDateTime.now());
                break;

            case "PAST":
                bookings = bookingRepository.findByBooker_IdAndEndBeforeOrderByStartDesc(bookerId,
                        LocalDateTime.now());
                break;
            default:
                throw new UnknownStateException("Unknown state: UNSUPPORTED_STATUS");
        }

        return bookings.stream()
                .map(bookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<BookingDto> getBookingsByOwnerId(Long ownerID, String state) {
        Collection<Booking> bookings;
        check(ownerID);
        switch (state) {
            case "WAITING":
                bookings = bookingRepository.findByItem_Owner_IdAndStatusOrderByStartDesc(ownerID, Status.WAITING);
                break;

            case "REJECTED":
                bookings = bookingRepository.findByItem_Owner_IdAndStatusOrderByStartDesc(ownerID, Status.REJECTED);
                break;

            case "ALL":
                bookings = bookingRepository.findByItem_Owner_IdOrderByStartDesc(ownerID);
                break;

            case "CURRENT":
                bookings = bookingRepository.findByItem_Owner_IdAndStartBeforeAndEndAfterOrderByStartDesc(ownerID,
                        LocalDateTime.now(), LocalDateTime.now());
                break;

            case "FUTURE":
                bookings = bookingRepository.findByItem_Owner_IdAndStartAfterOrderByStartDesc(ownerID,
                        LocalDateTime.now());
                break;

            case "PAST":
                bookings = bookingRepository.findByItem_Owner_IdAndEndBeforeOrderByStartDesc(ownerID,
                        LocalDateTime.now());
                break;
            default:
                throw new UnknownStateException("Unknown state: UNSUPPORTED_STATUS");
        }

        return bookings.stream()
                .map(bookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }


    @Override
    public ShortBookingDto getLastBooking(Long itemId) {
        return bookingMapper.toShortBookingDto(bookingRepository.findAllByItemId(itemId)
                .stream()
                .filter(booking -> booking.getStart().isBefore(LocalDateTime.now()))
                .max(Comparator.comparing(Booking::getStart)).orElse(null));
    }

    @Override
    public Booking getItemWithBooker(Long itemId, Long userID) {
        return bookingRepository.findFirstByItem_IdAndBookerIdAndEndIsBeforeAndStatus(itemId, userID,
                LocalDateTime.now(), Status.APPROVED);
    }

    @Override
    public ShortBookingDto getNextBooking(Long itemId) {
        ShortBookingDto lastBooking = getLastBooking(itemId);
        LocalDateTime end;
        if (lastBooking == null) {
            end = LocalDateTime.now();

        } else {
            end = lastBooking.getEnd();
        }
        return bookingMapper.toShortBookingDto(bookingRepository.findFirstByItem_IdAndStatusAndStartAfterOrderByStartAsc(itemId, Status.APPROVED, end).orElse(null));
    }

    private void check(Long userId) {
        if (userService.get(userId) == null) {
            throw new UserNotFoundException("User not found");
        }
    }
    ///

}
