package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.UserStorage;
import ru.yandex.practicum.filmorate.exceptions.AlreadyFriendsException;
import ru.yandex.practicum.filmorate.exceptions.IncorrectUserException;
import ru.yandex.practicum.filmorate.exceptions.NotFriendsException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validators.UserValidator;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Component
@Primary
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User getUser(long id) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM USERS where ID = ?", (rs, rowNum) -> makeUser(rs), id);
        } catch (EmptyResultDataAccessException e) {
            throw new IncorrectUserException(Long.toString(id));
        }
    }

    @Override
    public Collection<User> getUsers() {
        return jdbcTemplate.query("SELECT * FROM USERS", (rs, rowNum) -> makeUser(rs));
    }

    @Override
    public User updateUser(User user) throws ValidationException {
        UserValidator.userBasicValidation(user);

        jdbcTemplate.update("UPDATE USERS set LOGIN = ?, EMAIL = ?, NAME = ?, BIRTHDAY = ? where ID = ?",
                user.getLogin(),
                user.getEmail(),
                user.getName(),
                user.getBirthday(),
                user.getId()
        );

        return getUser(user.getId());
    }

    @Override
    public User addUser(User user) throws ValidationException, IncorrectUserException {
        UserValidator.userBasicValidation(user);
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement("INSERT INTO USERS(LOGIN, EMAIL, NAME, BIRTHDAY)" +
                    "values ( ?, ?, ?, ? )", new String[]{"id"});
            stmt.setString(1, user.getLogin());
            stmt.setString(2, user.getEmail());
            stmt.setString(3,
                    user.getName().isEmpty() || user.getName().isBlank() || user.getName() == null ?
                            user.getLogin() : user.getName());
            stmt.setDate(4, java.sql.Date.valueOf(user.getBirthday()));

            return stmt;
        }, keyHolder);

        return getUser((long) keyHolder.getKey());
    }

    @Override
    public Collection<User> getFriends(long userId) {
        return jdbcTemplate.query("SELECT * from USERS " +
                "WHERE id IN (select friend_id from friends where usr_id = ?)", (rs, rowNum) -> makeUser(rs), userId);
    }

    @Override
    public User addFriend(long userId, long friendId) throws IncorrectUserException,AlreadyFriendsException {
        try {
            getUser(userId);
        } catch (EmptyResultDataAccessException e) {
            throw new IncorrectUserException(Long.toString(userId));
        }

        try {
            getUser(friendId);
        } catch (EmptyResultDataAccessException e) {
            throw new IncorrectUserException(Long.toString(friendId));
        }

        int numOfEntities = jdbcTemplate.queryForObject("SELECT count(*) FROM friends " +
                "where usr_id = ? and friend_id = ?", Integer.class, userId, friendId);

        if(numOfEntities == 1) {
            throw new AlreadyFriendsException(Long.toString(friendId));
        }

        jdbcTemplate.update("INSERT INTO friends(usr_id, friend_id, approved) VALUES ( ?, ?, ? )",
                userId,
                friendId,
                0
        );

        return getUser(userId);
    }

    @Override
    public boolean deleteFriend(long userId, long friendId) throws NotFriendsException {
        int numOfEntities = jdbcTemplate.queryForObject("SELECT count(*) FROM friends " +
                "where usr_id = ? and friend_id = ?", Integer.class, userId, friendId);

        if(numOfEntities == 0) {
            throw new NotFriendsException("user: " + friendId + " | friend: " + userId);
        }

        return jdbcTemplate.update("DELETE FROM friends where usr_id = ? and friend_id = ?", userId, friendId) > 0;
    }

    private User makeUser(ResultSet rs) throws SQLException {
        User user = User.builder().build();
        int id = rs.getInt("id");
        String login = rs.getString("login");
        String email = rs.getString("email");
        String name = rs.getString("name");
        LocalDate birthday = rs.getDate("birthday").toLocalDate();

        user.setId(id);
        user.setLogin(login);
        user.setEmail(email);
        user.setName(name);
        user.setBirthday(birthday);

        return user;
    }
}