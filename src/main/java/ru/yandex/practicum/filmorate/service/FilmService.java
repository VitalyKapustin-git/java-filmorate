package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.IncorrectFilmException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.validators.UserExistsValidator;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserExistsValidator userExistsValidator;

    @Autowired
    FilmService(FilmStorage filmStorage, UserExistsValidator userExistsValidator) {
        this.filmStorage = filmStorage;
        this.userExistsValidator = userExistsValidator;
    }

    public Film getFilm(int id) {
        return filmStorage.getFilm(id);
    }

    public Collection<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Film addFilm(Film film) throws ValidationException {
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) throws ValidationException {
        Film film1 = filmStorage.getFilm(film.getId());
        if (film1 == null) throw new IncorrectFilmException(Long.toString(film.getId()));

        return filmStorage.updateFilm(film);
    }

    public void increaseRate(int filmId, int userId) {
        userExistsValidator.checkUser(userId);
        filmStorage.increaseRate(filmId, userId);
    }

    public void decreaseRate(int filmId, int userId) {
        userExistsValidator.checkUser(userId);
        filmStorage.decreaseRate(filmId, userId);
    }

    public long getRate(int filmId) {
        Film film = filmStorage.getFilm(filmId);
        if (film == null) throw new IncorrectFilmException(Long.toString(filmId));

        return getFilm(filmId).getRate();
    }

    public Collection<Film> getPopular(String count) {
        String filmsNumber;
        if (count.length() == 0) {
            filmsNumber = "1";
        } else {
            filmsNumber = count;
        }

        return filmStorage.getPopular(filmsNumber);
    }

    public Collection<Mpa> getAllMpa() {
        return filmStorage.getAllMpa();
    }

    public Mpa getMpa(int id) {
        return filmStorage.getMpa(id);
    }

    public List<Map<String, Object>> getAllGenres() {
        return filmStorage.getAllGenres();
    }

    public Map<String, Object> getGenre(int id) {
        return filmStorage.getGenre(id);
    }

}
