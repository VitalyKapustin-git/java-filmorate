package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

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
        return jdbcTemplate.queryForObject("SELECT * FROM FILMS F WHERE F.ID = ?", (rs, rowNum) -> makeFilm(rs), id);
    }

    @Override
    public Collection<Film> getFilms() {
        return jdbcTemplate.query("SELECT * FROM FILMS", (rs, rowNum) -> makeFilm(rs));
    }

    @Override
    public Film addFilm(Film film) throws ValidationException {
//        jdbcTemplate.update();

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement("INSERT INTO FILMS(RATE, NAME, DESCRIPTION, RELEASE_DATE, DURATION, MPA_ID)" +
                    "values ( ?, ?, ?, ?, ?, ? )", new String[]{"id"});
            stmt.setLong(1, film.getRate());
            stmt.setString(2, film.getName());
            stmt.setString(3, film.getDescription());
            stmt.setDate(4, java.sql.Date.valueOf(film.getReleaseDate()));
            stmt.setDouble(5, film.getDuration());
            stmt.setObject(6, film.getMpa().get("id"));

            return stmt;
        }, keyHolder);

//         Неправильно
        for(Map<String, Object> entity : film.getGenres()) {
            System.out.println(film.getId() + " and " + entity.get("id"));
            jdbcTemplate.update("INSERT INTO FILM_GENRE(FILM_ID, GENRE_ID) VALUES ( ?, ? )",
                    keyHolder.getKey().intValue(),
                    entity.get("id")
            );
        }



        return getFilm(keyHolder.getKey().intValue());
    }

    @Override
    public Film updateFilm(Film film) throws ValidationException {
        return null;
    }

    @Override
    public Map<String, Object> getMpa(int id) {
        return jdbcTemplate.queryForMap("SELECT m.id, m.mpa FROM MPA m where m.ID = ?", id);
    }

    @Override
    public List<Map<String, Object>> getAllMpa() {
        return jdbcTemplate.queryForList("SELECT id, mpa FROM MPA");
    }

    @Override
    public Map<String, Object> getGenre(int id) {
        return jdbcTemplate.queryForMap("SELECT id, genre from GENRES where ID = ?", id);
    }

    @Override
    public List<Map<String, Object>> getAllGenres() {
        return jdbcTemplate.queryForList("SELECT id, genre from GENRES");
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

        List genres = jdbcTemplate.queryForList(
                "SELECT G2.ID, G2.GENRE from FILM_GENRE fg " +
                        "JOIN GENRES G2 on fg.GENRE_ID = G2.ID WHERE fg.FILM_ID = ?", id);


        Map mpa = jdbcTemplate.queryForMap(
                "SELECT M.id, M.mpa FROM FILMS F " +
                        "join MPA M on M.ID = f.MPA_ID WHERE F.ID = ?", id);


        String name = rs.getString("name");
        String description = rs.getString("description");
        LocalDate releaseDate = rs.getDate("release_date").toLocalDate();
        Double duration = rs.getDouble("duration");

        film.setId(id);
        film.setRate(rate);
        film.setRates(rates);
        film.setGenres(genres);
        film.setMpa(mpa);
        film.setName(name);
        film.setDescription(description);
        film.setReleaseDate(releaseDate);
        film.setDuration(duration);

        return film;
    }
}