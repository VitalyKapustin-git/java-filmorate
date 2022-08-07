package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
    Film getFilm(int id);
    Collection<Film> getFilms();
    Film addFilm(Film film) throws ValidationException;
    Film updateFilm(Film film) throws ValidationException;
}