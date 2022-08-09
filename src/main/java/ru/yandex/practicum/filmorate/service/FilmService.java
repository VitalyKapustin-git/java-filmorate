package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.IncorrectFilmException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
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
        Film film = filmStorage.getFilm(id);
        if (film == null) throw new IncorrectFilmException(Integer.toString(id));

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
        Set<Integer> rates;
        Film film = filmStorage.getFilm(filmId);
        if (film == null) throw new IncorrectFilmException(Long.toString(filmId));
        userExistsValidator.checkUser(userId);

        if(film.getRates() == null) {
            rates = new HashSet<>();
        } else {
            rates = new HashSet<>(film.getRates());
        }

        rates.add(userId);
        film.setRates(rates);

        film.setRate(film.getRate() + 1);
    }

    public void decreaseRate(int filmId, int userId) {
        Film film = filmStorage.getFilm(filmId);
        if(film == null) throw new IncorrectFilmException(Long.toString(filmId));
        userExistsValidator.checkUser(userId);

        Set<Integer> rates = new HashSet<>(film.getRates());
        rates.remove(userId);
        film.setRates(rates);

        if (film.getRate() > 0) film.setRate(film.getRate() - 1);
    }

    public long getRate(int filmId) {
        Film film = filmStorage.getFilm(filmId);
        if (film == null) throw new IncorrectFilmException(Long.toString(filmId));

        return getFilm(filmId).getRate();
    }

    public Set<Film> getPopular(String count) {
        int filmsNumber = Integer.parseInt(count);

        System.out.println(filmStorage.getFilms());

        return filmStorage.getFilms().stream()
                .sorted(Comparator.comparingLong(Film::getRate).reversed())
                .limit(filmsNumber)
                .collect(Collectors.toSet());
    }

    public List<Map<String, Object>> getAllMpa() {
        return filmStorage.getAllMpa();
    }

    public Map<String, Object> getMpa(int id) {
        return filmStorage.getMpa(id);
    }

    public List<Map<String, Object>> getAllGenres() {
        return filmStorage.getAllGenres();
    }

    public Map<String, Object> getGenre(int id) {
        return filmStorage.getGenre(id);
    }

}
