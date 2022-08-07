package ru.yandex.practicum.filmorate.validators;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

@Slf4j
public class FilmValidator {
    public static void filmBasicValidation(Film film) throws ValidationException {
        if (film.getName().isBlank() || film.getName().isEmpty())
        {
            log.warn("Произошла ошибка при обновлении фильма (пустое имя фильма): {}", film);
            throw new ValidationException("Произошла ошибка при обновлении фильма (пустое имя фильма)");
        }
        if (film.getDescription().length() > 200)
        {
            log.warn("Произошла ошибка при обновлении фильма (описанее более 200 символов): {}", film);
            throw new ValidationException("Произошла ошибка при обновлении фильма (описанее более 200 символов)");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn("Произошла ошибка при обновлении фильма (фильм старее 28.12.1895): {}", film);
            throw new ValidationException("Произошла ошибка при обновлении фильма (фильм старее 28.12.1895)");
        }
        if (film.getDuration() <= 0 ) {
            log.warn("Произошла ошибка при обновлении фильма (продолжительность 0 или меньше): {}", film);
            throw new ValidationException("Произошла ошибка при обновлении фильма (продолжительность 0 или меньше)");
        }
    }
}
