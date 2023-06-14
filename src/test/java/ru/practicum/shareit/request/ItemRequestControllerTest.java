package ru.practicum.shareit.request;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.interfaces.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTest {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    @MockBean
    ItemRequestService service;

    private UserDto userDto;

    private ItemRequestDto item;


    @BeforeEach
    void create() {

        userDto = UserDto.builder()
                .id(1L)
                .name("Oleg")
                .email("oleg@mail.ru")
                .build();

        item = ItemRequestDto.builder()
                .id(1L)
                .description("description")
                .requestor(userDto.getId())
                .created(LocalDateTime.now())
                .items(null)
                .build();
    }

    @Test
    void shouldCreateRequest() throws Exception {
        Mockito

                .when(service.create(any(Long.class), any()))
                .thenReturn(item);

        mockMvc.perform(post("/requests")
                        .content(objectMapper.writeValueAsString(item))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(item.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(item.getDescription())))
                .andExpect(jsonPath("$.requestor", is(item.getRequestor()), Long.class))
                .andExpect(jsonPath("$.created",
                        is(item.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));
    }

    @Test
    void shouldGetRequestById() throws Exception {
        when(service.getById(any(Long.class), any(Long.class)))
                .thenReturn(item);

        mockMvc.perform(get("/requests/1")
                        .content(objectMapper.writeValueAsString(item))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(item.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(item.getDescription())))
                .andExpect(jsonPath("$.requestor", is(item.getRequestor()), Long.class))
                .andExpect(jsonPath("$.created",
                        is(item.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));
    }

    @Test
    void shouldGetAll() throws Exception {
        List<ItemRequestDto> list = new ArrayList<>();
        Mockito

                .when(service.getAll(any(Long.class), any(Integer.class), nullable(Integer.class)))
                .thenReturn(List.of(item));

        mockMvc.perform(get("/requests/all")
                        .content(objectMapper.writeValueAsString(list))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(item.getId()), Long.class))
                .andExpect(jsonPath("$.[0].description", is(item.getDescription())))
                .andExpect(jsonPath("$.[0].requestor", is(item.getRequestor()), Long.class))
                .andExpect(jsonPath("$.[0].created",
                        is(item.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));
    }

    @Test
    void shouldGetAllForOwner() throws Exception {
        List<ItemRequestDto> list = new ArrayList<>();
        Mockito

                .when(service.getForOwner(any(Long.class)))
                .thenReturn(List.of(item));

        mockMvc.perform(get("/requests")
                        .content(objectMapper.writeValueAsString(list))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(item.getId()), Long.class))
                .andExpect(jsonPath("$.[0].description", is(item.getDescription())))
                .andExpect(jsonPath("$.[0].requestor", is(item.getRequestor()), Long.class))
                .andExpect(jsonPath("$.[0].created",
                        is(item.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));
    }
}
