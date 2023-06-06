package ru.practicum.shareit.user;


import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.dto.UserDto;


import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserServiceTest {


    private final UserServiceImpl userService;


    private UserDto userTest1 = new UserDto();
    private UserDto userTest2 = new UserDto();


    @BeforeEach
    public void create() {
        userTest1.setId(1L);
        userTest1.setName("Dmitry");
        userTest1.setEmail("aaa@mail.ru");


        userTest2.setId(2L);
        userTest2.setName("Vladimir");
        userTest2.setEmail("bbb@mail.ru");

    }
    @Test
    @Order(1)
    void shouldAddUserTest() {
        UserDto checkUser = userService.create(userTest1);

        assertEquals(userTest1, checkUser);
    }

    @Test
    @Order(3)
    void shouldUpdateUser() {
        UserDto userForUp = new UserDto();
        userForUp.setEmail("xxx@mail.ru");


        assertEquals(userService.update(userForUp,1L),userForUp);
    }



    @Test
    @Order(2)
    void shouldGetUser() {
        assertEquals(userService.getUserById(userTest1.getId()), userTest1);

    }

    @Test
    @Order(4)
    void shouldGetAllUsers() {
        List<UserDto> users = new ArrayList<>();
        users.add(userTest1);
        users.add(userTest2);

        assertEquals(users.size(), 2);
    }

    @Test
    @Order(5)
    void shouldRemoveUser() {
        List<UserDto> users = new ArrayList<>();
        users.add(userTest1);

        assertEquals(users.size(),1);

        userService.remove(userTest1.getId());
        users.remove(userTest1);

        assertEquals(users.size(),0);
    }




}
