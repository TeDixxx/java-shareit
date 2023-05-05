package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.interfaces.UserStorage;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;

@Slf4j
@Component("inMemoryUserStorage")
public class InMemoryUserStorage implements UserStorage {

    private final HashMap<Long, User> users = new HashMap<>();
    private Long currentId = 1L;

    @Override
    public User create(User user) throws ValidationException {
        checkDuplicateEmail(user);
        user.setId(currentId++);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) throws ValidationException {
        checkDuplicateEmail(user);
        users.put(user.getId(), user);
        return user;

    }

    @Override
    public void remove(Long userId) {
        users.remove(userId);
    }

    @Override
    public User getUserById(Long userId) {
        if (users.containsKey(userId)) {
            return users.get(userId);
        } else {
            log.warn("User with id {} not found.", userId);
            throw new UserNotFoundException("User not found. Invalid id");
        }

    }

    @Override
    public Collection<User> getAllUsers() {
        return users.values();
    }

    public void checkDuplicateEmail(User user) throws ValidationException {
        for (User values : getAllUsers()) {
            if (user.getEmail().equals(values.getEmail()) && !Objects.equals(user.getId(), values.getId())) {
                throw new ValidationException("Duplicate email");
            }
        }

    }
}
