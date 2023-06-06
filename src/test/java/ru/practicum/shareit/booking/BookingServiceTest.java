package ru.practicum.shareit.booking;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.DateBookingDto;

import ru.practicum.shareit.booking.dto.Status;
import ru.practicum.shareit.booking.interfaces.BookingRepository;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.interfaces.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.interfaces.UserRepository;
import ru.practicum.shareit.user.model.User;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    @InjectMocks
    BookingServiceImpl bookingService;

    @Mock
    UserRepository userRepository;

    @Mock
    ItemRepository itemRepository;

    @Mock
    BookingRepository bookingRepository;

    @Mock
    ItemMapper itemMapper;

    private DateBookingDto dateBookingDto;
    private ItemDto itemDto;
    private UserDto userDto;

    @BeforeEach
    void create() {
        itemDto = ItemDto.builder()
                .id(1L)
                .name("spear")
                .description("divine spear")
                .available(true)
                .build();

        userDto = UserDto.builder()
                .id(1L)
                .name("phantom lancer")
                .email("icefrog@mail.ru")
                .build();

        dateBookingDto = DateBookingDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(1))
                .build();


    }

    @Test
    void shouldCreateBooking() {
        User user = UserMapper.fromUserDto(userDto);
        Item item = itemMapper.fromItemDto(itemDto, user);

        item.setOwner(user);

        when(userRepository.findById(2L))
                .thenReturn(Optional.of(new User()));

        when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item));

        when(bookingRepository.save(Mockito.any(Booking.class)))
                .thenReturn(new Booking());

        BookingDto bookingDto = bookingService.create(dateBookingDto, 2L);

        assertNotNull(bookingDto);

    }

    @Test
    void shouldGetBookingByOwnerId() {

        int from = 0;
        int size = 10;


        List<BookingDto> bookings = bookingService.getBookingsByOwnerId(1L, "ALL", size, from);

        assertThat(bookings).hasSize(5);

    }

    @Test
    void shouldGetBookingByBookerId() {
        int from = 0;
        int size = 10;

        List<BookingDto> bookings = bookingService.getBookingsByBookerId(2L, "ALL", size, from);
        assertThat(bookings).hasSize(5);
    }

    @Test
    void shouldAccept() {
        User user = UserMapper.fromUserDto(userDto);
        Item item = itemMapper.fromItemDto(itemDto, user);
        Booking booking = BookingMapper.toBooking(dateBookingDto, item, user);

        item.setOwner(user);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(new User()));

        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));

        when(bookingRepository.save(Mockito.any(Booking.class)))
                .thenReturn(new Booking());

        BookingDto bookingDto = bookingService.toAccept(1L, 1L, true);

        assertNotNull(bookingDto);
        assertEquals(bookingDto.getStatus(), Status.APPROVED);

        booking.setStatus(Status.WAITING);

        bookingDto = bookingService.toAccept(1L, 1L, false);

        assertNotNull(bookingDto);
        assertEquals(bookingDto.getStatus(), Status.REJECTED);
    }
}
