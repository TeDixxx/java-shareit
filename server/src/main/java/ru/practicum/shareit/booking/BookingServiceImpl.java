package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.dto.*;
import ru.practicum.shareit.booking.interfaces.BookingRepository;
import ru.practicum.shareit.booking.interfaces.BookingService;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.interfaces.ItemRepository;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.interfaces.UserRepository;

import ru.practicum.shareit.user.model.User;


import java.time.LocalDateTime;
import java.util.Comparator;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    @Autowired
    private final BookingRepository bookingRepository;

    @Autowired
    private final ItemRepository itemRepository;

    @Autowired
    private final UserRepository userRepository;

    @Override
    public BookingDto create(DateBookingDto dateBookingDto, Long bookerId) {

        Item item = itemRepository.findById(dateBookingDto.getItemId()).orElseThrow(()
                -> new ItemNotFoundException("item not found"));

        User user = userRepository.findById(bookerId).orElseThrow(()
                -> new UserNotFoundException("User not found"));

        if (!item.getAvailable()) {
            throw new UnAvailableException("Item not available");
        }

        if (item.getOwner().getId().equals(bookerId)) {
            throw new UserNotFoundException("owner can't booking his item");
        }

        Booking booking = BookingMapper.toBooking(dateBookingDto, item, user);

        if (dateBookingDto.getEnd().isBefore(dateBookingDto.getStart())
                || dateBookingDto.getEnd().equals(dateBookingDto.getStart())) {
            throw new IncorrectDataException("Incorrect time for booking");
        }

        bookingRepository.save(booking);

        return BookingMapper.toBookingDto(booking);
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

        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public BookingDto findBookingByOwnerIdOrBookerId(Long userId, Long bookingId) {
        check(userId);
        Booking booking = bookingRepository.findBookingByOwnerOrBooker(userId, bookingId).orElseThrow(()
                -> new BookingNotFoundException("Booking not found"));
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getBookingsByBookerId(Long bookerId, String state, int size, int from) {
        List<Booking> bookings;
        check(bookerId);

        if (from < 0 || size < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        PageRequest page = PageRequest.of(from == 0 ? 0 : (from / size), size);

        switch (state) {
            case "WAITING":
                bookings = bookingRepository.findByBooker_IdAndStatusOrderByStartDesc(bookerId, Status.WAITING, page);
                break;

            case "REJECTED":
                bookings = bookingRepository.findByBooker_IdAndStatusOrderByStartDesc(bookerId, Status.REJECTED, page);
                break;

            case "ALL":
                bookings = bookingRepository.findByBooker_IdOrderByStartDesc(bookerId, page);
                break;

            case "CURRENT":
                bookings = bookingRepository.findByBooker_IdAndStartBeforeAndEndAfterOrderByStartDesc(bookerId,
                        LocalDateTime.now(), LocalDateTime.now(), page);
                break;

            case "FUTURE":
                bookings = bookingRepository.findByBooker_IdAndStartAfterOrderByStartDesc(bookerId,
                        LocalDateTime.now(), page);
                break;

            case "PAST":
                bookings = bookingRepository.findByBooker_IdAndEndBeforeOrderByStartDesc(bookerId,
                        LocalDateTime.now(), page);
                break;
            default:
                throw new UnknownStateException("Unknown state: UNSUPPORTED_STATUS");
        }

        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getBookingsByOwnerId(Long ownerID, String state, int size, int from) {
        List<Booking> bookings;
        check(ownerID);

        if (from < 0 || size < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        PageRequest page = PageRequest.of(from, size);

        switch (state) {
            case "WAITING":
                bookings = bookingRepository.findByItem_Owner_IdAndStatusOrderByStartDesc(ownerID, Status.WAITING, page);
                break;

            case "REJECTED":
                bookings = bookingRepository.findByItem_Owner_IdAndStatusOrderByStartDesc(ownerID, Status.REJECTED, page);
                break;

            case "ALL":
                bookings = bookingRepository.findByItem_Owner_IdOrderByStartDesc(ownerID, page);
                break;

            case "CURRENT":
                bookings = bookingRepository.findByItem_Owner_IdAndStartBeforeAndEndAfterOrderByStartDesc(ownerID,
                        LocalDateTime.now(), LocalDateTime.now(), page);
                break;

            case "FUTURE":
                bookings = bookingRepository.findByItem_Owner_IdAndStartAfterOrderByStartDesc(ownerID,
                        LocalDateTime.now(), page);
                break;

            case "PAST":
                bookings = bookingRepository.findByItem_Owner_IdAndEndBeforeOrderByStartDesc(ownerID,
                        LocalDateTime.now(), page);
                break;
            default:
                throw new UnknownStateException("Unknown state: UNSUPPORTED_STATUS");
        }

        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }


    @Override
    public ShortBookingDto getLastBooking(Long itemId) {
        return BookingMapper.toShortBookingDto(bookingRepository.findAllByItemId(itemId)
                .stream()
                .filter(booking -> booking.getStart().isBefore(LocalDateTime.now()))
                .max(Comparator.comparing(Booking::getStart)).orElse(null));
    }

    @Override
    public Booking getBookedItemWithBooker(Long itemId, Long userID) {
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
        return BookingMapper.toShortBookingDto(bookingRepository.findFirstByItem_IdAndStatusAndStartAfterOrderByStartAsc(itemId, Status.APPROVED, end).orElse(null));
    }

    private void check(Long userId) {
        userRepository.findById(userId).orElseThrow(()
                -> new UserNotFoundException("User not found"));
    }

}
