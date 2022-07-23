package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static ru.yandex.practicum.filmorate.validators.UserValidator.userBasicValidation;

@Component
@Slf4j
@Primary
public class InMemoryUserStorage implements UserStorage {
    private int userIdCounter;
    private final Map<Long, User> userList;

    InMemoryUserStorage() {
        this.userIdCounter = 1;
        this.userList = new HashMap<>();
    }

    @Override
    public User getUser(long userId) {
        return userList.get(userId);
    }

    @Override
    public Collection<User> getUsers() {
        return userList.values();
    }

    @Override
    public User updateUser(User user) throws ValidationException {
        userBasicValidation(user);

        if (user.getId() < 1) {
            log.warn("Произошла ошибка при обновлении пользователя (некорректный id пользователя): {}", user);
            throw new ValidationException("Произошла ошибка при обновлении пользователя (некорректный id пользователя)");
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

    @Override
    public User addUser(User user) throws ValidationException {
        userBasicValidation(user);

        user.setId(userIdCounter++);
        if(user.getName().isBlank() || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        userList.put(user.getId(), user);

        log.info("Успешно создан пользователь -> {}", user);

        return user;
    }
}