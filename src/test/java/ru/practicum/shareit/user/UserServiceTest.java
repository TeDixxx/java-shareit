package ru.practicum.shareit.user;


import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserServiceTest {

    @Autowired
    private UserServiceImpl userService;


    private UserDto userTest1;
    private User user;
    private UserDto userTest2;


    @BeforeEach
    public void create() {
        userTest1 = UserDto.builder()
                .id(1L)
                .name("Dmitry")
                .email("aaa@mail.ru")
                .build();

        userService.create(userTest1);


        userTest2 = UserDto.builder()
                .id(2L)
                .name("Vladimir")
                .email("bbb@mail.ru").build();

        user = User.builder()
                .id(6L)
                .name("Valera")
                .email("kkk@mail.tu")
                .build();


        userService.create(userTest2);


    }

    @Test
    @Order(1)
    void shouldAddUserTest() {
        assertNotNull(userTest1);
        assertEquals(1L, userTest1.getId());
        assertEquals("Dmitry", userTest1.getName());
        assertEquals("aaa@mail.ru", userTest1.getEmail());
    }

    @Test
    @Order(3)
    void shouldUpdateUser() {
        UserDto userForUp = new UserDto();
        userForUp.setEmail("xxx@mail.ru");
        assertEquals(userService.update(userForUp, 1L), userForUp);

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, ()
                -> userService.update(userForUp, 80L));

        assertEquals(exception.getMessage(), "User not found!");
    }


    @Test
    @Order(2)
    void shouldGetUser() {
        assertEquals(userService.getUserById(userTest1.getId()), userTest1);

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, ()
                -> userService.getUserById(70L));

        assertEquals("Пользователь не найден", exception.getMessage());

    }

    @Test
    @Order(4)
    void shouldGetAllUsers() {
        Collection<UserDto> users = userService.getAllUsers();
        assertEquals(users.size(), 2);
    }

    @Test
    @Order(5)
    void shouldRemoveUser() {
        List<UserDto> users = new ArrayList<>();
        users.add(userTest1);

        assertEquals(users.size(), 1);

        userService.remove(userTest1.getId());
        users.remove(userTest1);

        assertEquals(users.size(), 0);
    }


    @Test
    void testUserClass() {
        assertEquals(user.getEmail(), "kkk@mail.tu");
        assertEquals(user.getName(), "Valera");

        user.setEmail("sss@mail.ru");

        assertEquals(user.getEmail(), "sss@mail.ru");
    }

    @Test
    void shouldGet() {

        assertEquals(userService.get(userService.create(UserMapper.toUserDto(user)).getId()).getId(), 3L);

    }

}
