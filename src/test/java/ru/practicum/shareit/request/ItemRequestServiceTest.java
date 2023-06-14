package ru.practicum.shareit.request;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.exceptions.ItemRequestNotFoundException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.interfaces.UserService;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ItemRequestServiceTest {

    @Autowired
    private ItemRequestServiceImpl service;

    @Autowired
    UserService userService;


    private UserDto userDto1;
    private UserDto userDto2;

    private ItemRequestDto item1;
    private ItemRequestDto item2;

    private ItemRequest itemRequest;


    @BeforeEach
    void create() {

        userDto1 = UserDto.builder()
                .id(1L)
                .name("Oleg")
                .email("oleg@mail.ru")
                .build();

        userDto2 = UserDto.builder()
                .id(2L)
                .name("Ivan")
                .email("ivan@mail.ru")
                .build();

        item1 = ItemRequestDto.builder()
                .id(1L)
                .description("description")
                .requestor(userDto1.getId())
                .created(LocalDateTime.now())
                .items(null)
                .build();

        item2 = ItemRequestDto.builder()
                .id(2L)
                .description("description")
                .requestor(userDto2.getId())
                .created(LocalDateTime.now())
                .items(null)
                .build();


        itemRequest = ItemRequest.builder()
                .id(6L)
                .description("descr")
                .requestor(UserMapper.fromUserDto(userDto1))
                .created(LocalDateTime.now().plusHours(3))
                .build();

        userService.create(userDto1);
        userService.create(userDto2);


    }

    @Test
    void shouldCreateRequest() {

        ItemRequestDto itemRequestDto = service.create(userDto1.getId(), item1);

        assertEquals(itemRequestDto.getDescription(), item1.getDescription());
        assertEquals(itemRequestDto.getRequestor(), item1.getRequestor());
        assertEquals(itemRequestDto.getId(), item1.getId());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, ()
                -> service.create(90L, item1));
        assertEquals("User not found", exception.getMessage());

    }

    @Test
    void shouldGetById() {
        ItemRequestDto itemRequestDto = service.create(userDto2.getId(), item2);
        assertEquals(service.getById(userDto2.getId(), itemRequestDto.getId()).getId(), 1L);

        ItemRequestNotFoundException exception = assertThrows(ItemRequestNotFoundException.class, ()
                -> service.getById(userDto2.getId(), 34L));
        assertEquals("Request not found", exception.getMessage());

        UserNotFoundException exception1 = assertThrows(UserNotFoundException.class, ()
                -> service.getById(23L, itemRequestDto.getId()));

        assertEquals("User not found", exception1.getMessage());

    }

    @Test
    void shouldGetAll() {
        ItemRequestDto request1 = service.create(userDto1.getId(), item1);
        ItemRequestDto request2 = service.create(userDto2.getId(), item2);

        List<ItemRequestDto> requests = service.getAll(userDto1.getId(), 0, 1);

        assertEquals(1, requests.size());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, ()
                -> service.getAll(56L, 0, 1));

        assertEquals("User not found", exception.getMessage());

        ResponseStatusException exception1 = assertThrows(ResponseStatusException.class, ()
                -> service.getAll(userDto1.getId(), -1, 0));

        assertEquals("400 BAD_REQUEST", exception1.getMessage());


    }

    @Test
    void shouldGetForOwner() {
        ItemRequestDto request1 = service.create(userDto1.getId(), item1);
        ItemRequestDto request2 = service.create(userDto1.getId(), item2);

        List<ItemRequestDto> requests = service.getForOwner(userDto1.getId());

        assertEquals(2, requests.size());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, ()
                -> service.getForOwner(10L));
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void testItemRequestClass() {
        assertEquals(itemRequest.getDescription(), "descr");

        itemRequest.setDescription("эти тесты доходят до абсурда");

        assertEquals(itemRequest.getDescription(), "эти тесты доходят до абсурда");
    }


}
