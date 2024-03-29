package ru.yandex.practicum.filmorate.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.*;

@RestController
public class FilmController {
    private final FilmService filmService;

    @Autowired
    FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping("/films/{id}")
    public Film getFilm(@PathVariable int id) {
        return filmService.getFilm(id);
    }

    @GetMapping("/films/popular")
    public Collection<Film> getPopular(@RequestParam(name = "count",
            required = false,
            defaultValue = "10") String count) {
        return filmService.getPopular(count);
    }

    @GetMapping("/films")
    public Collection<Film> getFilms()  {
        return filmService.getFilms();
    }

    @PutMapping("/films/{id}/like/{userId}")
    public void addLike(@PathVariable int id, @PathVariable int userId) {filmService.increaseRate(id, userId);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public void removeLike(@PathVariable int id, @PathVariable int userId) {filmService.decreaseRate(id, userId);
    }

    @PutMapping("/films")
    public Film updateFilm(@Valid @RequestBody Film film) throws ValidationException {
        return filmService.updateFilm(film);
    }

    @PostMapping("/films")
    public Film addFilm(@Valid @RequestBody Film film) throws ValidationException {
        return filmService.addFilm(film);
    }

    @GetMapping("/mpa")
    public Collection<Mpa> getAllMpa() {
        return filmService.getAllMpa();
    }

    @GetMapping("/mpa/{id}")
    public Mpa getMpa(@PathVariable int id) {
        return filmService.getMpa(id);
    }

    @GetMapping("/genres")
    public List<Map<String, Object>> getAllGenre() {
        return filmService.getAllGenres();
    }

    @GetMapping("/genres/{id}")
    public Map<String, Object> getGenre(@PathVariable int id) {
        return filmService.getGenre(id);
    }


}
