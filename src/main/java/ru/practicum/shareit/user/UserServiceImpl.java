package ru.practicum.shareit.user;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.interfaces.UserRepository;
import ru.practicum.shareit.user.interfaces.UserService;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.stream.Collectors;


@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDto create(UserDto userDto) {
        return UserMapper.toUserDto(userRepository.save(UserMapper.fromUserDto(userDto)));
    }

    @Override
    public UserDto update(UserDto userDto, Long userId) {

        userDto.setId(userId);

        User user = userRepository.findById(userDto.getId()).orElseThrow(()
                -> new UserNotFoundException("User not found!"));

        if (userDto.getEmail() == null) {
            userDto.setEmail(user.getEmail());
        }

        if (userDto.getName() == null) {
            userDto.setName(user.getName());
        }

        return UserMapper.toUserDto(userRepository.save(UserMapper.fromUserDto(userDto)));
    }

    @Override
    public void remove(Long userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public UserDto getUserById(Long userId) {
        return UserMapper.toUserDto(userRepository.findById(userId).orElseThrow(()
                -> new UserNotFoundException("Пользователь не найден")));
    }

    @Override
    public Collection<UserDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public User get(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Not found"));
    }


}
