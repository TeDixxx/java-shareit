package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.interfaces.UserService;
import ru.practicum.shareit.user.interfaces.UserStorage;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.Collection;


@Service
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    public UserServiceImpl(@Qualifier("inMemoryUserStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public UserDto create(UserDto userDto) throws ValidationException {
        User newUser = userStorage.create(UserMapper.fromUserDto(userDto));
        return UserMapper.toUserDto(newUser);
    }

    @Override
    public UserDto update(UserDto userDto, Long userId) throws ValidationException {
        User user = userStorage.getUserById(userId);

        if (userDto.getEmail() == null) {
            userDto.setEmail(user.getEmail());
        }

        if (userDto.getName() == null) {
            userDto.setName(user.getName());
        }

        userDto.setId(userId);

        userStorage.update(UserMapper.fromUserDto(userDto));


        return userDto;
    }

    @Override
    public void remove(Long userId) {
        User user = userStorage.getUserById(userId);
        userStorage.remove(user.getId());
    }

    @Override
    public UserDto getUserById(Long userId) throws UserNotFoundException {
        return UserMapper.toUserDto(userStorage.getUserById(userId));
    }

    @Override
    public Collection<UserDto> getAllUsers() {
        Collection<User> users = userStorage.getAllUsers();
        Collection<UserDto> usersDto = new ArrayList<>();

        for (User user : users) {
            usersDto.add(UserMapper.toUserDto(user));
        }
        return usersDto;
    }


}
