package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.AlreadyFriendsException;
import ru.yandex.practicum.filmorate.exceptions.IncorrectUserException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

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
        User user = userStorage.getUser(id);
        if (user == null) throw new IncorrectUserException(Integer.toString(id));

        return user;
    }

    public User createUser(User user) throws ValidationException {
        return userStorage.addUser(user);
    }

    public User updateUser(User user) throws ValidationException {
        User user1 = userStorage.getUser(user.getId());
        if (user1 == null || user == null) throw new IncorrectUserException(Long.toString(user.getId()));

        return userStorage.updateUser(user);
    }

    public Collection<User> getAllUsers() {
        return userStorage.getUsers();
    }

    public User addFriend(long userId, long friendId) {
        User user = userStorage.getUser(userId);
        User friend = userStorage.getUser(friendId);

        if(user == null || friend == null) {
            throw new IncorrectUserException(Long.toString(userId));
        }

        if(user.getFriends() == null) {
            user.setFriends(Set.of(friendId));
        } else if (user.getFriends().contains(friendId)) {
            throw new AlreadyFriendsException(user.getFriends().toString());
        } else {
            Set<Long> oldFriendList = new HashSet<>(user.getFriends());
            oldFriendList.add(friend.getId());
            user.setFriends(oldFriendList);
        }

        if(friend.getFriends() == null) {
            friend.setFriends(Set.of(userId));
        } else if (friend.getFriends().contains(userId)) {
            throw new AlreadyFriendsException(friend.getFriends().toString());
        } else {
            Set<Long> oldFriendList = new HashSet<>(friend.getFriends());
            oldFriendList.add(user.getId());
            friend.setFriends(oldFriendList);
        }

        return userStorage.getUser(userId);
    }

    public void deleteFriend(long userId, long friendId) {

        if(userStorage.getUser(userId) == null || userStorage.getUser(friendId) == null) {
            throw new IncorrectUserException(Long.toString(userId));
        }

        userStorage.getUser(userId)
                .getFriends()
                .remove(friendId);

        userStorage.getUser(friendId)
                .getFriends()
                .remove(userId);
    }

    public Set<User> getFriends(long userId) {

        if(userStorage.getUser(userId) == null) {
            throw new IncorrectUserException(Long.toString(userId));
        }

        if(userStorage.getUser(userId).getFriends() == null) {
            return new HashSet<>();
        }

        return userStorage.getUser(userId).getFriends().stream()
                .map(userStorage::getUser)
                .collect(Collectors.toSet());
    }

    public Set<User> getMutual(long userId, long friendId) {

        if(userStorage.getUser(userId) == null || userStorage.getUser(friendId) == null) {
            throw new IncorrectUserException(Long.toString(userId));
        }

        return getFriends(userId).stream()
                .filter(getFriends(friendId)::contains)
                .collect(Collectors.toSet());
    }
}