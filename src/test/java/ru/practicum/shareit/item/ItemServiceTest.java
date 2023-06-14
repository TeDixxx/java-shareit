package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.context.annotation.Lazy;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.DateBookingDto;
import ru.practicum.shareit.booking.interfaces.BookingService;


import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.interfaces.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.interfaces.UserService;
import ru.practicum.shareit.user.model.User;


import java.time.LocalDateTime;
import java.util.Collection;

import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ItemServiceTest {

    @Autowired
    @Lazy
    private ItemService itemService;

    @Autowired
    private UserService userService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private ItemMapper itemMapper;

    User user;
    UserDto userDto;
    UserDto userDto2;

    ItemDto spear;
    ItemDto axe;
    Item item;


    @BeforeEach
    void create() {
        user = User.builder()
                .id(1L)
                .name("Dmitry")
                .email("xxx@mail.ru")
                .build();

        userDto = UserDto.builder()
                .id(2L)
                .name("Alex")
                .email("email@mail.ru")
                .build();

        userDto2 = UserDto.builder()
                .id(3L)
                .name("Pedro")
                .email("aaa@mail.ru")
                .build();

        spear = ItemDto.builder()
                .id(1L)
                .name("spear")
                .description("divine spear")
                .available(true)
                .owner(user)
                .build();

        axe = ItemDto.builder()
                .id(2L)
                .name("axe")
                .description("axe is GOD!")
                .available(true)
                .owner(user)
                .build();

        item = Item.builder()
                .id(7L)
                .name("null")
                .description("descr")
                .owner(user)
                .available(true)
                .build();

    }

    @Test
    void shouldCreateItem() {
        UserDto testUser = userService.create(userDto);
        ItemDto testItem = itemService.add(spear, testUser.getId());
        ItemDto testItem2 = itemService.getItemById(testItem.getId(), testUser.getId());

        assertEquals(testItem2.getId(), spear.getId());
        assertEquals(testItem.getDescription(), spear.getDescription());
        assertEquals(testItem.getName(), spear.getName());

        spear.setAvailable(null);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, ()
                -> itemService.add(spear, testUser.getId()));

        assertEquals("400 BAD_REQUEST", exception.getMessage());

    }

    @Test
    void shouldGetItemById() {
        UserDto owner = userService.create(userDto);

        ItemDto itemDto = itemService.add(axe, owner.getId());

        assertEquals("axe", itemService.getItemById(itemDto.getId(), owner.getId()).getName());
        assertEquals("axe is GOD!", itemService.getItemById(itemDto.getId(), owner.getId()).getDescription());
        assertEquals(true, itemService.getItemById(itemDto.getId(), owner.getId()).getAvailable());
    }

    @Test
    void shouldUpdateItem() {
        UserDto owner = userService.create(userDto2);
        ItemDto itemDto = itemService.add(spear, owner.getId());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, ()
                -> itemService.update(itemDto, 60L, itemDto.getId()));
        assertEquals("Only owner can change item!", exception.getMessage());


        ItemDto itemForUp = itemService.update(itemDto, owner.getId(), itemDto.getId());

        assertEquals("spear", itemForUp.getName());
        assertEquals("divine spear", itemForUp.getDescription());
        assertEquals(true, itemForUp.getAvailable());
    }

    @Test
    void shouldGetItemByOwnerId() {
        UserDto owner = userService.create(userDto2);
        ItemDto itemDto = itemService.add(spear, owner.getId());
        ItemDto itemDto2 = itemService.add(axe, owner.getId());

        Collection<ItemDto> ownerItems = itemService.getItemsByOwnerId(owner.getId());

        assertEquals(2, ownerItems.size());
    }

    @Test
    void shouldFindItemByText() {
        UserDto owner = userService.create(userDto2);
        ItemDto itemDto = itemService.add(axe, owner.getId());

        Collection<ItemDto> searchItems = itemService.search("axe");

        assertEquals(1, searchItems.size());
    }

    @Test
    void shouldCreateComment() {
        UserDto owner = userService.create(userDto2);
        UserDto commentator = userService.create(userDto);

        ItemDto itemDto = itemService.add(spear, owner.getId());

        DateBookingDto dateBookingDto;
        dateBookingDto = DateBookingDto.builder()
                .itemId(itemDto.getId())
                .start(LocalDateTime.now().plusSeconds(1))
                .end(LocalDateTime.now().plusSeconds(3))
                .build();

        BookingDto bookingDto = bookingService.create(dateBookingDto, commentator.getId());
        bookingService.toAccept(bookingDto.getId(), owner.getId(), true);

        try {
            sleep(5000);
        } catch (InterruptedException runtimeException) {
            throw new RuntimeException(runtimeException);
        }

        CommentDto commentDto;
        commentDto = CommentDto.builder()
                .id(1L)
                .text("really divine spear!")
                .item(itemMapper.fromItemDto(itemDto, UserMapper.fromUserDto(owner)))
                .authorName(commentator.getName())
                .created(LocalDateTime.now())
                .build();

        itemService.createComment(itemDto.getId(), commentator.getId(), commentDto);

        assertEquals(1, itemService.getCommentsByItemId(itemDto.getId()).size());
    }

    @Test
    void shouldGet() {
        userService.create(UserMapper.toUserDto(user));
        assertEquals(itemService.get(itemService.add(itemMapper.toItemDto(item), user.getId()).getId()).getId(), 1);
    }


}
