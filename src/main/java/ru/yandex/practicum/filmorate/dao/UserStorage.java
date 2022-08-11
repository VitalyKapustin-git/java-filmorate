package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    User getUser(long id);
    Collection<User> getUsers();
    User updateUser(User user) throws ValidationException;
    User addUser(User user) throws ValidationException;

    User addFriend(long userId, long friendId);

    boolean deleteFriend(long userId, long friendId);

    Collection<User> getFriends(long userId);
}
