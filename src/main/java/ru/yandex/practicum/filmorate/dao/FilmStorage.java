package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface FilmStorage {
    Film getFilm(int id);
    Collection<Film> getFilms();
    Film addFilm(Film film) throws ValidationException;
    Film updateFilm(Film film) throws ValidationException;

    Mpa getMpa(int id);

    Collection<Mpa> getAllMpa();

    Map<String, Object> getGenre(int id);

    List<Map<String, Object>> getAllGenres();
}