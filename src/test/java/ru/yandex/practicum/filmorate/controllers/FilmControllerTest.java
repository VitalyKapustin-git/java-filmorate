package ru.yandex.practicum.filmorate.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.util.NestedServletException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validators.FilmValidator;

import java.time.LocalDate;

@SpringBootTest
@AutoConfigureMockMvc
public class FilmControllerTest {

    @Autowired
    private MockMvc mockMvc;
    private String film1JsonString;
    private Film film1;
    private Gson gson;

    @BeforeEach
    public void beforeEach() {

        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter().nullSafe()).create();

        film1 = Film.builder()
                .name("Фильм 1")
                .description("Описание фильма 1")
                .releaseDate(LocalDate.of(2022, 5, 10))
                .duration(30)
                .build();
    }

    // ТЕСТЫ РЕАЛИЗОВАНЫ ДОПОЛЬНИТЕЛЬНО ЧЕРЕЗ MockMvc ТАМ, ГДЕ ЭТО ВОЗМОЖНО
    // (соответственно тесты покрывают не только исключения, но и 4хх ошибки)

    // VALIDATOR TESTS

    @Test
    public void testValidatorEmptyName() {
        film1.setName("");
        try {
            FilmValidator.filmBasicValidation(film1);
        } catch (ValidationException e) {
            assertEquals("Произошла ошибка при обновлении фильма (пустое имя фильма)", e.getMessage());
        }
    }

    @Test
    public void testValidatorOversymbDescr() {
        film1.setDescription("......................................................................" +
                "......................................................................" +
                "......................................................................" +
                "......................................................................");
        try {
            FilmValidator.filmBasicValidation(film1);
        } catch (ValidationException e) {
            assertEquals("Произошла ошибка при обновлении фильма (описанее более 200 символов)", e.getMessage());
        }    }
    @Test
    public void testValidatorPastTime() {
        film1.setReleaseDate(LocalDate.of(1700, 2, 13));
        try {
            FilmValidator.filmBasicValidation(film1);
        } catch (ValidationException e) {
            assertEquals("Произошла ошибка при обновлении фильма (фильм старее 28.12.1895)", e.getMessage());
        }    }
    @Test
    public void testValidatorDuration() {
        film1.setDuration(-14324);
        try {
            FilmValidator.filmBasicValidation(film1);
        } catch (ValidationException e) {
            assertEquals("Произошла ошибка при обновлении фильма (продолжительность 0 или меньше)", e.getMessage());
        }    }


    // API TESTS
    @Test
    @DisplayName("Создание фильма")
    public void mustCreateFilmSuccessfully() throws Exception {
        film1JsonString = gson.toJson(film1);

        mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .content(film1JsonString)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Фильм 1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("Фильм с пустым именем не должен создаваться")
    public void mustReturn400onEmptyName() throws Exception {
        film1.setName("");
        film1JsonString = gson.toJson(film1);

        mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .content(film1JsonString)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    @DisplayName("Описание более 200 символов не должно приниматься")
    public void mustReturn400onLimitDescription() throws Exception {
        film1.setDescription("......................................................................" +
                "......................................................................" +
                "......................................................................" +
                "......................................................................");
        film1JsonString = gson.toJson(film1);

        mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .content(film1JsonString)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    @DisplayName("Неположительная продолжительность фильма не должна приниматься")
    public void mustReturn400onNegativeDuration() throws Exception {
        film1.setDuration(-100);
        film1JsonString = gson.toJson(film1);

        mockMvc.perform(MockMvcRequestBuilders.put("/films")
                        .content(film1JsonString)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    @DisplayName("Фильм старее 1895го года не должен приниматься")
    public void mustReturn400onReleaseDate() throws Exception {
        film1.setReleaseDate(LocalDate.of(1894, 12, 28));
        film1JsonString = gson.toJson(film1);

        try{
            mockMvc.perform(MockMvcRequestBuilders.put("/films")
                            .content(film1JsonString)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andReturn();
        } catch (NestedServletException e) {
            assertThrows(ValidationException.class, () -> {throw e.getCause();});
        }
    }
}
