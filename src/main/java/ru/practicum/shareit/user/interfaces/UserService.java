package ru.practicum.shareit.user.interfaces;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;


import java.util.Collection;

@Service
public interface UserService {

    UserDto create(UserDto userDto);

    UserDto update(UserDto userDto, Long userId);

    void remove(Long userId);

    UserDto getUserById(Long userId);

    Collection<UserDto> getAllUsers();


}
