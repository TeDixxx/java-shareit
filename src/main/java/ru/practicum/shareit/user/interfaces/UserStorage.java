package ru.practicum.shareit.user.interfaces;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserStorage {
    User create(User user);

    User update(User user);

    void remove(Long userId);

    User getUserById(Long userId);

    Collection<User> getAllUsers();


}
