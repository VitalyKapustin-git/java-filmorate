//package ru.yandex.practicum.filmorate.storage.film;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.context.annotation.Primary;
//import org.springframework.stereotype.Component;
//import ru.yandex.practicum.filmorate.dao.FilmStorage;
//import ru.yandex.practicum.filmorate.exceptions.ValidationException;
//import ru.yandex.practicum.filmorate.model.Film;
//
//import java.util.Collection;
//import java.util.HashMap;
//import java.util.Map;
//
//import static ru.yandex.practicum.filmorate.validators.FilmValidator.filmBasicValidation;
//
//@Component
//@Slf4j
//public class InMemoryFilmStorage implements FilmStorage {
//    private final Map<Integer, Film> filmList;
//    private int filmIdCounter;
//
//    InMemoryFilmStorage() {
//        this.filmList = new HashMap<>();
//        this.filmIdCounter = 1;
//    }
//
//    @Override
//    public Film getFilm(int id) {
//        return filmList.get(id);
//    }
//
//    @Override
//    public Collection<Film> getFilms() {
//        return filmList.values();
//    }
//
//    @Override
//    public Film updateFilm(Film film) throws ValidationException {
//        filmBasicValidation(film);
//
//        if (film.getId() < 1) {
//            log.warn("Произошла ошибка при обновлении фильма (некорректный id < 1): {}", film);
//            throw new ValidationException("Произошла ошибка при обновлении фильма (некорректный id < 1)");
//        }
//
//        if (filmList.containsKey(film.getId())) {
//            filmList.put(film.getId(), film);
//        } else {
//            film.setId(filmIdCounter++);
//            filmList.put(film.getId(), film);
//        }
//
//        log.info("Успешно обновлен фильм -> {}", film);
//
//        return film;
//    }
//
//    @Override
//    public Film addFilm(Film film) throws ValidationException {
//        filmBasicValidation(film);
//
//        film.setId(filmIdCounter++);
//        filmList.put(film.getId(), film);
//
//        log.info("Успешно добавлен фильм -> {}", film);
//
//        return film;
//    }
//
//}
