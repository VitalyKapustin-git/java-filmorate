package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final Map<Integer, Film> filmList = new HashMap<>();
    private int filmIdCounter = 1;

    @GetMapping
    public Collection<Film> getFilms() {
        return filmList.values();
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) throws ValidationException {

        if (
                film.getName().isBlank()                        ||
                film.getName().isEmpty()                ||
                film.getDescription().length() > 200    ||
                film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28)) ||
                film.getDuration() <= 0 ||
                        film.getId() < 1
        ) {
            log.warn("Произошла ошибка при обновлении фильма: {}", film);
            throw new ValidationException("Произошла ошибка при обновлении фильма");
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

        if (
                film.getName().isBlank()                        ||
                film.getName().isEmpty()                ||
                film.getDescription().length() > 200    ||
                film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28)) ||
                film.getDuration() <= 0
        ) {
            log.warn("Произошла ошибка при добавлении фильма: {}", film);
            throw new ValidationException("Произошла ошибка при добавлении фильма");
        }

        film.setId(filmIdCounter++);
        filmList.put(film.getId(), film);

        log.info("Успешно добавлен фильм -> {}", film);

        return film;
    }
}
