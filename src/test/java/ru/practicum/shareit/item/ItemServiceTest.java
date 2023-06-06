package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.crossstore.ChangeSetPersister;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.DateBookingDto;
import ru.practicum.shareit.booking.dto.ShortBookingDto;
import ru.practicum.shareit.booking.interfaces.BookingRepository;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.interfaces.CommentRepository;
import ru.practicum.shareit.item.interfaces.ItemRepository;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.interfaces.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.interfaces.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {

    @Mock
    ItemRepository itemRepository;
    @Mock
    ItemMapper itemMapper;

    @Mock
    UserRepository userRepository;

    @Mock
    CommentRepository commentRepository;

    @Mock
    BookingRepository bookingRepository;

    @Mock
    ItemRequestRepository itemRequestRepository;

    @InjectMocks
    ItemServiceImpl itemService;

    Item item = new Item();
    ItemDto itemDto = new ItemDto();

    User user = new User();
    UserDto userDto = new UserDto();

    Booking booking = new Booking();
    DateBookingDto dateBookingDto = new DateBookingDto();

    Comment comment = new Comment();
    CommentDto commentDto = new CommentDto();

    ItemRequest itemRequest = new ItemRequest();
    ItemRequestDto itemRequestDto = new ItemRequestDto();


    @BeforeEach
    void create() {

        userDto.setId(1L);
        userDto.setName("Vladimir");
        userDto.setEmail("myemail@mail.ru");

        user = UserMapper.fromUserDto(userDto);

        itemDto.setId(1L);
        itemDto.setName("itemDto");
        itemDto.setDescription("description");
        itemDto.setAvailable(true);
        itemDto.setRequestId(1L);
        itemDto.setOwner(user);

        item = itemMapper.fromItemDto(itemDto, user);

        dateBookingDto.setItemId(item.getId());
        dateBookingDto.setStart(LocalDateTime.now());
        dateBookingDto.setEnd(LocalDateTime.now().plusDays(1));

        booking = BookingMapper.toBooking(dateBookingDto, item, user);

        comment.setId(1L);
        comment.setText("text");
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());

        commentDto = itemMapper.toCommentDto(comment);

        itemRequestDto.setId(1L);
        itemRequestDto.setDescription("description");
        itemRequestDto.setCreated(LocalDateTime.now());
        itemRequestDto.setItems(List.of(itemDto));

        itemRequest = ItemRequestMapper.fromItemRequestDto(itemRequestDto, user);

    }

    @Test
    void shouldCreateItem() {
        Mockito

                .when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));

        assertThrows(UserNotFoundException.class, () -> itemService.add(itemDto, 2L));

        Mockito

                .when(itemRequestRepository.findById(1L))
                .thenReturn(Optional.of(itemRequest));

        Mockito.when(itemRepository.save(Mockito.any()))
                .thenReturn(item);

        itemDto.setRequestId(2L);

     assertThrows(NullPointerException.class, () -> itemService.add(itemDto, userDto.getId()));

     itemDto.setRequestId(1L);

     ItemDto result = itemService.add(itemDto, userDto.getId());

     assertNotNull(result);

    }

    @Test
    void shouldGetItemById() {
    }

    @Test
    void shouldUpdateItem() {
    }

    @Test
    void shouldGetItemByOwnerId() {
    }

    @Test
    void shouldFindItemByText() {
    }

    @Test
    void shouldCreateComment() {
    }


}
