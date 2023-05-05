package ru.practicum.shareit.user.interfaces;


import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

@Service
public interface UserService {

    UserDto create(UserDto userDto) throws ValidationException;

    UserDto update(UserDto userDto, Long userId) throws ValidationException;

    void remove(Long userId);

    UserDto getUserById(Long userId);

    Collection<UserDto> getAllUsers();


}
