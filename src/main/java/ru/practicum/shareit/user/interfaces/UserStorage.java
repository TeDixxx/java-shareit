package ru.practicum.shareit.user.interfaces;

import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserStorage {
    User create(User user) throws ValidationException;

    User update(User user) throws ValidationException;

    void remove(Long userId);

    User getUserById(Long userId);

    Collection<User> getAllUsers();


}
