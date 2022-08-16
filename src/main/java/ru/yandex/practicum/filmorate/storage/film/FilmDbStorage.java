package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.exceptions.IncorrectFilmException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.validators.FilmValidator;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Component
@Primary
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film getFilm(int id) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM FILMS F WHERE F.ID = ?",
                    (rs, rowNum) -> makeFilm(rs), id);
        } catch (EmptyResultDataAccessException e) {
            throw new IncorrectFilmException(Integer.toString(id));
        }
    }

    @Override
    public Collection<Film> getFilms() {
        return jdbcTemplate.query("SELECT * FROM FILMS", (rs, rowNum) -> makeFilm(rs));
    }

    @Override
    public Film addFilm(Film film) throws ValidationException {
        FilmValidator.filmBasicValidation(film);

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO FILMS(RATE, NAME, DESCRIPTION, RELEASE_DATE, DURATION, MPA_ID)" +
                    "values ( ?, ?, ?, ?, ?, ? )", new String[]{"id"});
            stmt.setLong(1, film.getRate());
            stmt.setString(2, film.getName());
            stmt.setString(3, film.getDescription());
            stmt.setDate(4, java.sql.Date.valueOf(film.getReleaseDate()));
            stmt.setDouble(5, film.getDuration());
            stmt.setObject(6, film.getMpa().get("id"));

            return stmt;
        }, keyHolder);

        if(film.getGenres() != null) {
            for (Map<String, Object> entity : film.getGenres()) {
                jdbcTemplate.update("INSERT INTO FILM_GENRE(FILM_ID, GENRE_ID) VALUES ( ?, ? )",
                        Objects.requireNonNull(keyHolder.getKey()).intValue(),
                        entity.get("id")
                );
            }
        }

        return getFilm(Objects.requireNonNull(keyHolder.getKey()).intValue());
    }

    @Override
    public Collection<Film> getPopular(String count) {
        int filmsNumber = Integer.parseInt(count);

        return jdbcTemplate.query("SELECT * FROM FILMS ORDER BY RATE, RELEASE_DATE DESC LIMIT ?",
                (rs, rowNumber) -> makeFilm(rs), filmsNumber);
    }

    @Override
    public void increaseRate(int filmId, int userId) {
        jdbcTemplate.update("MERGE INTO FILM_RATES(usr_id, film_id) VALUES ( ?, ? )", userId, filmId);
        jdbcTemplate.update("UPDATE FILMS SET RATE = RATE + 1");
    }

    @Override
    public void decreaseRate(int filmId, int userId) {
        jdbcTemplate.update("DELETE FROM FILM_RATES WHERE FILM_ID = ? and USR_ID = ? ", filmId, userId);
        jdbcTemplate.update("UPDATE FILMS SET RATE = RATE - 1");
    }

    @Override
    public Film updateFilm(Film film) {
        try {
            jdbcTemplate.update("UPDATE FILMS set RATE = ?, NAME = ?, DESCRIPTION = ?, " +
                            "RELEASE_DATE = ?, DURATION = ?, MPA_ID = ? where ID = ?",
                film.getRate(),
                film.getName(),
                film.getDescription(),
                java.sql.Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa().get("id"),
                film.getId()
            );

            if (film.getGenres() != null) {
                updateFilmGenre(film);
            }

            return getFilm(film.getId());
        } catch (EmptyResultDataAccessException e) {
            throw new IncorrectFilmException(Integer.toString(film.getId()));
        }
    }

    @Override
    public Mpa getMpa(int id) {
        try {
            return jdbcTemplate.queryForObject("SELECT m.id, m.NAME FROM MPA m where m.ID = ?",
                    (rs, rowNum) -> makeMpa(rs), id);
        } catch (EmptyResultDataAccessException e) {
            throw new IncorrectFilmException(Integer.toString(id));
        }
    }

    @Override
    public Collection<Mpa> getAllMpa() {
        return jdbcTemplate.query("SELECT * FROM MPA",
                (rs, rowNum) -> makeMpa(rs));
    }

    @Override
    public Map<String, Object> getGenre(int id) {
        try {
            Map<String, Object> genre = jdbcTemplate.queryForMap("SELECT id, name from GENRES where ID = ?", id);
            genre = mapToLowerCase(genre);
            return genre;
        } catch (EmptyResultDataAccessException e) {
            throw new IncorrectFilmException(Integer.toString(id));
        }
    }

    @Override
    public List<Map<String, Object>> getAllGenres() {
        List<Map<String, Object>> genresLower = new ArrayList<>();
        List<Map<String, Object>> genres = jdbcTemplate.queryForList("SELECT id, name from GENRES");
        for(Map<String, Object> entity : genres) {
            genresLower.add(mapToLowerCase(entity));
        }

        return genresLower;
    }

    private Mpa makeMpa(ResultSet rs) throws SQLException {
        Mpa mpa = Mpa.builder().build();

        int id = rs.getInt("id");
        String mpaName = rs.getString("name");

        mpa.setId(id);
        mpa.setName(mpaName);

        return mpa;
    }

    private Film makeFilm(ResultSet rs) throws SQLException {
        Film film = Film.builder().build();
        int id = rs.getInt("id");

        long rate = rs.getLong("rate");

        SqlRowSet ratesRowSet = jdbcTemplate.queryForRowSet("SELECT USR_ID FROM FILM_RATES WHERE FILM_ID = ?", id);
        Set<Integer> rates = new HashSet<>();
        while (ratesRowSet.next()) {
            rates.add(ratesRowSet.getInt("usr_id"));
        }

        List<Map<String, Object>> genres = jdbcTemplate.queryForList(
                "SELECT G2.ID, G2.NAME from FILM_GENRE fg " +
                        "JOIN GENRES G2 on fg.GENRE_ID = G2.ID WHERE fg.FILM_ID = ?", id);
        List<Map<String, Object>> genresLower = new ArrayList<>();
        for(Map<String, Object> entity : genres) {
            genresLower.add(mapToLowerCase(entity));
        }
        
        Map<String, Object> mpa = jdbcTemplate.queryForMap(
                "SELECT M.ID, M.NAME FROM FILMS F " +
                        "join MPA M on M.ID = F.MPA_ID WHERE F.ID = ?", id);

        mpa = mapToLowerCase(mpa);

        String name = rs.getString("name");
        String description = rs.getString("description");
        LocalDate releaseDate = rs.getDate("release_date").toLocalDate();
        double duration = rs.getDouble("duration");

        film.setId(id);
        film.setRate(rate);
        film.setRates(rates);
        film.setGenres(genresLower);
        film.setMpa(mpa);
        film.setName(name);
        film.setDescription(description);
        film.setReleaseDate(releaseDate);
        film.setDuration(duration);

        return film;
    }

    public void updateFilmGenre(Film film) {
        if (film.getGenres().isEmpty()) {
            int countGenres = jdbcTemplate.queryForObject("SELECT count(*) FROM FILM_GENRE " +
                    "WHERE FILM_ID = ?", Integer.class, film.getId());

            if (countGenres > 0) {
                jdbcTemplate.update("DELETE FROM FILM_GENRE WHERE film_id = ?", film.getId());
            }
        } else {
            jdbcTemplate.update("DELETE FROM FILM_GENRE WHERE film_id = ? AND genre_id NOT IN (?)",
                    film.getId(),
                    film.getGenres().stream()
                            .map(mapObj -> (int) mapObj.get("id")).toArray()
            );

            film.getGenres().forEach(v -> jdbcTemplate.update("MERGE INTO FILM_GENRE(film_id, genre_id) " +
                    "VALUES ( ?, ? ) ;", film.getId(), v.get("id")));
        }
    }

    private Map<String, Object> mapToLowerCase(Map<String, Object> map) {
        Map<String, Object> mapLower = new HashMap<>();
        for (String key : map.keySet()) {
            mapLower.put(key.toLowerCase(), map.get(key));
        }

        return mapLower;
    }
}