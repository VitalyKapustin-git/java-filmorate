package ru.yandex.practicum.filmorate.validators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.IncorrectUserException;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

@Service
public class UserExistsValidator {
    private final UserStorage userStorage;

    @Autowired
    UserExistsValidator(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void checkUser(int userId) {
        if(userStorage.getUser(userId) == null) throw new IncorrectUserException(Long.toString(userId));
    }
}