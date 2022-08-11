package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class FilmorateApplicationTests {
	private final UserDbStorage userStorage;
	private final FilmDbStorage filmStorage;

	@Test
	public void testFindUserById() {
		Optional<User> userOptional = Optional.of(userStorage.getUser(1L));

		assertNotNull(userOptional);
		assertEquals(1, userOptional.get().getId());
		assertEquals("vkapustin", userOptional.get().getLogin());
	}

	@Test
	public void getAllUsers() {
		ArrayList<User> users = new ArrayList<>(userStorage.getUsers());

		assertEquals(2, users.size());

		assertEquals("ipetrov", users.get(1).getLogin());
		assertEquals("vkapustin", users.get(0).getLogin());
	}

	@Test
	public void updateUser() throws ValidationException {
		LocalDate localDate = LocalDate.of(1995, 12, 20);
		LocalDate newBirthday = LocalDate.of(1945, 5, 10);
		User dbUser = userStorage.getUser(1);

		assertEquals("vkapustin", dbUser.getLogin());
		assertEquals(localDate, dbUser.getBirthday());

		User updatedUser = User.builder().build();
		updatedUser.setId(dbUser.getId());
		updatedUser.setName(dbUser.getName());
		updatedUser.setEmail(dbUser.getEmail());
		updatedUser.setLogin(dbUser.getLogin());
		updatedUser.setBirthday(newBirthday);

		userStorage.updateUser(updatedUser);

		assertEquals(newBirthday, userStorage.getUser(1).getBirthday());
	}

	@Test
	public void addUser() throws ValidationException {
		LocalDate birthday = LocalDate.of(1956, 7, 12);
		User newUser = User.builder().build();
		newUser.setName("opop");
		newUser.setLogin("testUser");
		newUser.setEmail("testUser@example.com");
		newUser.setBirthday(birthday);

		userStorage.addUser(newUser);

		User newDbUser = userStorage.getUser(3);

		assertEquals(3, userStorage.getUsers().size());
		assertEquals("testUser", newDbUser.getLogin());
		assertEquals("opop", newDbUser.getName());
		assertEquals("testUser@example.com", newDbUser.getEmail());
		assertEquals(birthday, newDbUser.getBirthday());


	}

	// Здесь сразу покрыто и addFriend и getFriends
	@Test
	public void addAndGetFriend() {
		userStorage.addFriend(1L, 2L);

		assertEquals(2L, new ArrayList<>(userStorage.getFriends(1L)).get(0).getId());
	}

	@Test
	public void deleteFriend() {
		userStorage.addFriend(1L, 2L);
		assertEquals(2L, new ArrayList<>(userStorage.getFriends(1L)).get(0).getId());

		assertTrue(userStorage.deleteFriend(1L, 2L));
		assertEquals(0, new ArrayList<>(userStorage.getFriends(1L)).size());
	}

	// ФИЛЬМЫ
	@Test
	public void getFilm() {
		Film film = filmStorage.getFilm(1);

		assertEquals("Дети шпионов", film.getName());
	}

	@Test
	public void getFilms() {
		ArrayList<Film> films = new ArrayList<>(filmStorage.getFilms());
		List<String> filmsName = films.stream().map(Film::getName).collect(Collectors.toList());

		assertEquals(3, films.size());
		assertTrue(filmsName.containsAll(List.of("Гарри Поттер и Философский камень", "Дети шпионов", "Гарри Поттер и Тайная комната")));
	}

	@Test
	public void addFilm() throws ValidationException {
		Film newFilm = Film.builder().build();
		newFilm.setName("Гарри Поттер и Узник Азкабана");
		newFilm.setRate(7);
		newFilm.setDescription("Третий год обучения в школе хогвартс");
		newFilm.setDuration(142);
		newFilm.setReleaseDate(LocalDate.of(2004, 5, 23));
		newFilm.setMpa(Map.of("id", 2));

		filmStorage.addFilm(newFilm);
		ArrayList<Film> films = new ArrayList<>(filmStorage.getFilms());
		List<String> filmsName = films.stream().map(Film::getName).collect(Collectors.toList());

		assertTrue(filmsName.contains("Гарри Поттер и Узник Азкабана"));
	}

	@Test
	public void updateFilm() {
		Film oldFilm = filmStorage.getFilm(2);

		Film updatedFilm = Film.builder().build();
		updatedFilm.setId(oldFilm.getId());
		updatedFilm.setName(oldFilm.getName());
		updatedFilm.setRate(oldFilm.getRate());
		updatedFilm.setDescription("Обновленное описание фильма!!");
		updatedFilm.setDuration(oldFilm.getDuration());
		updatedFilm.setReleaseDate(oldFilm.getReleaseDate());
		updatedFilm.setMpa(oldFilm.getMpa());

		filmStorage.updateFilm(updatedFilm);

		assertEquals("Обновленное описание фильма!!", filmStorage.getFilm(2).getDescription());
	}

	@Test
	public void getMpa() {
		assertEquals("G", filmStorage.getMpa(1).getName());
		assertEquals("PG", filmStorage.getMpa(2).getName());
		assertEquals("PG-13", filmStorage.getMpa(3).getName());
		assertEquals("R", filmStorage.getMpa(4).getName());
	}

	@Test
	public void getAllMpa() {
		ArrayList<Mpa> mpa = new ArrayList<>(filmStorage.getAllMpa());

		assertEquals(5, mpa.size());
		assertTrue(mpa.stream().map(Mpa::getName).collect(Collectors.toList()).contains("NC-17"));
	}

	@Test
	public void getGenre() {
		Map<String, Object> genre = filmStorage.getGenre(1);

		assertEquals("Комедия", genre.get("name"));
	}

	@Test
	public void getAllGenres() {
		List<Map<String, Object>> allGenres = filmStorage.getAllGenres();
		List<String> genresName = allGenres.stream().map(map -> map.get("name").toString()).collect(Collectors.toList());

		assertEquals(6, allGenres.size());
		assertTrue(genresName.containsAll(List.of("Комедия", "Драма", "Мультфильм", "Триллер", "Документальный", "Боевик")));
	}
}