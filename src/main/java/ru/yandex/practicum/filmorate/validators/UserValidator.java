package ru.yandex.practicum.filmorate.validators;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

@Slf4j
public class UserValidator {
    public static void userBasicValidation(User user) throws ValidationException {
        if (user.getEmail().isBlank() || user.getEmail().isEmpty())
        {
            log.warn("Произошла ошибка при обновлении пользователя (пустой email): {}", user);
            throw new ValidationException("Произошла ошибка при обновлении пользователя (пустой email)");
        }
        if (!user.getEmail().contains("@"))
        {
            log.warn("Произошла ошибка при обновлении пользователя (некорректный email): {}", user);
            throw new ValidationException("Произошла ошибка при обновлении пользователя (некорректный email)");
        }
        if (user.getLogin().isBlank() || user.getLogin().isEmpty()) {
            log.warn("Произошла ошибка при обновлении пользователя (пустой логин): {}", user);
            throw new ValidationException("Произошла ошибка при обновлении пользователя (пустой логин)");
        }
        if (user.getLogin().contains(" ")) {
            log.warn("Произошла ошибка при обновлении пользователя (логин содержит пробелы): {}", user);
            throw new ValidationException("Произошла ошибка при обновлении пользователя (логин содержит пробелы)");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Произошла ошибка при обновлении пользователя (день рождения в будущем): {}", user);
            throw new ValidationException("Произошла ошибка при обновлении пользователя (день рождения в будущем)");
        }
    }
}
