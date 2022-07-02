package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    Map<Integer, User> userList = new HashMap<>();
    int userIdCounter = 1;

    @GetMapping
    public Collection<User> getUsers() {
        return userList.values();
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) throws ValidationException {
        if (
                        user.getEmail().isBlank()      ||
                        user.getEmail().isEmpty()      ||
                        !user.getEmail().contains("@")   ||
                        user.getLogin().isBlank()      ||
                        user.getLogin().isEmpty()      ||
                        user.getLogin().contains(" ")  ||
                        user.getBirthday().isAfter(LocalDate.now()) ||
                                user.getId() < 1

        ) {
            log.warn("Произошла ошибка при обновлении пользователя: {}", user);
            throw new ValidationException("Произошла ошибка при обновлении пользователя");
        }

        if(user.getName().isBlank() || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }

        if (userList.containsKey(user.getId())) {
            userList.put(user.getId(), user);
        } else {
            user.setId(userIdCounter++);
            userList.put(user.getId(), user);
        }

        log.info("Успешно обновлен пользователь -> {}", user);

        return user;
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User user) throws ValidationException {
        if (
                user.getEmail().isBlank()      ||
                        user.getEmail().isEmpty()      ||
                        !user.getEmail().contains("@")   ||
                        user.getLogin().isBlank()      ||
                        user.getLogin().isEmpty()      ||
                        user.getLogin().contains(" ")  ||
                        user.getBirthday().isAfter(LocalDate.now())
        ) {
            log.warn("Произошла ошибка при создании пользователя: {}", user);
            throw new ValidationException("Произошла ошибка при создании пользователя");
        }

        user.setId(userIdCounter++);
        if(user.getName().isBlank() || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        userList.put(user.getId(), user);

        log.info("Успешно создан пользователь -> {}", user);

        return user;
    }
}
