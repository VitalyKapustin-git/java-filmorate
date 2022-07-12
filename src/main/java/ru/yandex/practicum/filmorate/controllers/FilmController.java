package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final Map<Integer, Film> filmList = new HashMap<>();
    private int filmIdCounter = 1;

    public void filmBasicValidation(Film film) throws ValidationException {
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

    @GetMapping
    public Collection<Film> getFilms() {
        return filmList.values();
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) throws ValidationException {
        filmBasicValidation(film);

        if (film.getId() < 1) {
            log.warn("Произошла ошибка при обновлении фильма (некорректный id < 1): {}", film);
            throw new ValidationException("Произошла ошибка при обновлении фильма (некорректный id < 1)");
        }

        if (filmList.containsKey(film.getId())) {
            filmList.put(film.getId(), film);
        } else {
            film.setId(filmIdCounter++);
            filmList.put(film.getId(), film);
        }

        log.info("Успешно обновлен фильм -> {}", film);

        return film;
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) throws ValidationException {
        filmBasicValidation(film);

        film.setId(filmIdCounter++);
        filmList.put(film.getId(), film);

        log.info("Успешно добавлен фильм -> {}", film);

        return film;
    }
}
