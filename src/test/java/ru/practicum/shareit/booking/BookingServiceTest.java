package ru.practicum.shareit.booking;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Lazy;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.dto.*;


import ru.practicum.shareit.booking.interfaces.BookingService;
import ru.practicum.shareit.exceptions.BookingNotFoundException;
import ru.practicum.shareit.exceptions.UnknownStateException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;

import ru.practicum.shareit.item.interfaces.ItemService;


import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import ru.practicum.shareit.user.interfaces.UserService;



import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@Transactional
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class BookingServiceTest {

    @Autowired
    BookingService bookingService;

    @Autowired
    UserService userService;

    @Autowired
    ItemService itemService;

    @Autowired
    @Lazy
    ItemMapper itemMapper;

    @Autowired
    @Lazy
    BookingMapper bookingMapper;

    private DateBookingDto dateBookingDto;
    private ItemDto itemDto;
    private UserDto owner;
    private UserDto booker;
    private ShortBookingDto shortBooking;
    BookingDto bookingDto;

    @BeforeEach
    void create() {
        owner = UserDto.builder()
                .id(1L)
                .name("phantom lancer")
                .email("icefrog@mail.ru")
                .build();

        itemDto = ItemDto.builder()
                .id(1L)
                .name("spear")
                .description("divine spear")
                .available(true)
                .owner(UserMapper.fromUserDto(owner))
                .nextBooking(null)
                .lastBooking(null)
                .comments(null)
                .requestId(null)
                .build();

        booker = UserDto.builder()
                .id(2L)
                .name("Spectre")
                .email("dagger@mail.ru")
                .build();

        dateBookingDto = DateBookingDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(1))
                .build();

        shortBooking = ShortBookingDto.builder()
                .id(1L)
                .bookerId(booker.getId())
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(2))
                .build();


        userService.create(owner);
        userService.create(booker);

        itemService.add(itemDto, owner.getId());

        bookingDto = bookingService.create(dateBookingDto, booker.getId());


    }

    @Test
    void shouldCreateBooking() {
        assertEquals(bookingDto.getBooker().getId(), 2L);
        assertEquals(bookingDto.getItem().getName(), itemDto.getName());
        assertEquals(bookingDto.getItem().getId(), itemDto.getId());


        UserNotFoundException exception = assertThrows(UserNotFoundException.class, ()
                -> bookingService.create(dateBookingDto, 5L));
        assertEquals("User not found", exception.getMessage());

    }

    @Test
    void shouldAcceptBooking() {
        BookingDto test = bookingService.toAccept(1L, 1L, true);
        assertEquals(Status.APPROVED, test.getStatus());
    }

    @Test
    void shouldGetBookingByUserIdOrBookerId() {
        BookingDto dto = bookingService.create(new DateBookingDto(1L, LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(3)), booker.getId());
        assertEquals(bookingService.findBookingByOwnerIdOrBookerId(1L, 2L), dto);

        BookingNotFoundException exception = assertThrows(BookingNotFoundException.class, ()
                -> bookingService.findBookingByOwnerIdOrBookerId(1L, 10L));

        assertEquals("Booking not found", exception.getMessage());

    }

    @Test
    void shouldGetBookingByBookerId() {
        BookingDto dto = bookingService.create(new DateBookingDto(1L, LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(3)), booker.getId());

        assertEquals(List.of(dto), bookingService.getBookingsByBookerId(booker.getId(), "WAITING", 1, 0));
        assertEquals(new ArrayList<>(), bookingService.getBookingsByBookerId(booker.getId(), "REJECTED", 1, 0));
        assertEquals(new ArrayList<>(), bookingService.getBookingsByBookerId(booker.getId(), "PAST", 1, 0));
        assertEquals(List.of(dto), bookingService.getBookingsByBookerId(booker.getId(), "FUTURE", 1, 0));
        assertEquals(List.of(bookingDto), bookingService.getBookingsByBookerId(booker.getId(), "CURRENT", 1, 0));
        assertEquals(List.of(dto), bookingService.getBookingsByBookerId(booker.getId(), "ALL", 1, 0));

        UnknownStateException exception1 = assertThrows(UnknownStateException.class, ()
                -> bookingService.getBookingsByBookerId(booker.getId(), "XXX", 1, 0));
        assertEquals("Unknown state: UNSUPPORTED_STATUS", exception1.getMessage());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, ()
                -> bookingService.getBookingsByBookerId(booker.getId(), "WAITING", 0, -1));
        assertEquals("400 BAD_REQUEST", exception.getMessage());

    }

    @Test
    void shouldGetBookingByOwnerId() {
        BookingDto dto = bookingService.create(new DateBookingDto(1L, LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(3)), booker.getId());

        assertEquals(List.of(dto), bookingService.getBookingsByOwnerId(owner.getId(), "WAITING", 1, 0));
        assertEquals(new ArrayList<>(), bookingService.getBookingsByOwnerId(owner.getId(), "REJECTED", 1, 0));
        assertEquals(new ArrayList<>(), bookingService.getBookingsByOwnerId(owner.getId(), "PAST", 1, 0));
        assertEquals(List.of(dto), bookingService.getBookingsByOwnerId(owner.getId(), "FUTURE", 1, 0));
        assertEquals(List.of(bookingDto), bookingService.getBookingsByOwnerId(owner.getId(), "CURRENT", 1, 0));
        assertEquals(List.of(dto), bookingService.getBookingsByOwnerId(owner.getId(), "ALL", 1, 0));

        UnknownStateException exception1 = assertThrows(UnknownStateException.class, ()
                -> bookingService.getBookingsByOwnerId(owner.getId(), "XXX", 1, 0));
        assertEquals("Unknown state: UNSUPPORTED_STATUS", exception1.getMessage());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, ()
                -> bookingService.getBookingsByOwnerId(owner.getId(), "WAITING", 0, -1));
        assertEquals("400 BAD_REQUEST", exception.getMessage());
    }

}
