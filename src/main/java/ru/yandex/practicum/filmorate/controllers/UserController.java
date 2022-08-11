package ru.yandex.practicum.filmorate.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
public class UserController {
    private final UserService userService;

    @Autowired
    UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users/{id}/friends")
    public Collection<User> getFriends(@PathVariable int id) {
        return userService.getFriends(id);
    }

    @GetMapping("/users/{id}")
    public User getUser(@PathVariable int id) {
        return userService.getUser(id);
    }

    @GetMapping("/users/{id}/friends/common/{otherId}")
    public Collection<User> getMutual(@PathVariable int id, @PathVariable int otherId) {
        return userService.getMutual(id, otherId);
    }

    @GetMapping("/users")
    public Collection<User> getUsers() {
        return userService.getAllUsers();
    }

    @PutMapping("/users/{id}/friends/{friendId}")
    public User addFriend(@PathVariable long id, @PathVariable long friendId) {
        return userService.addFriend(id, friendId);
    }

    @DeleteMapping("/users/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable long id, @PathVariable long friendId) {
        userService.deleteFriend(id, friendId);
    }

    @PutMapping("/users")
    public User updateUser(@Valid @RequestBody User user) throws ValidationException {
        return userService.updateUser(user);
    }

    //TODO Убрать возможность создавать одного и того же пользователя
    @PostMapping("/users")
    public User createUser(@Valid @RequestBody User user) throws ValidationException {
        return userService.createUser(user);
    }
}
