package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.dao.UserStorage;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public class UserDbStorage implements UserStorage {
    @Override
    public User getUser(long id) {
        return null;
    }

    @Override
    public Collection<User> getUsers() {
        return null;
    }

    @Override
    public User updateUser(User user) throws ValidationException {
        return null;
    }

    @Override
    public User addUser(User user) throws ValidationException {
        return null;
    }
}
