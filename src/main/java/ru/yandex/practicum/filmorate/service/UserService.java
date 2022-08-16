package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.dao.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User getUser(int id) {
        return userStorage.getUser(id);
    }

    public User createUser(User user) throws ValidationException {
        return userStorage.addUser(user);
    }

    public User updateUser(User user) throws ValidationException {
        return userStorage.updateUser(user);
    }

    public Collection<User> getAllUsers() {
        return userStorage.getUsers();
    }

    public User addFriend(long userId, long friendId) {
        return userStorage.addFriend(userId, friendId);
    }

    public boolean deleteFriend(long userId, long friendId) {
        return userStorage.deleteFriend(userId, friendId);
    }

    public Collection<User> getFriends(long userId) {
        return userStorage.getFriends(userId);
    }

    public Set<User> getMutual(long userId, long friendId) {
        return getFriends(userId).stream()
                .filter(getFriends(friendId)::contains)
                .collect(Collectors.toSet());
    }
}