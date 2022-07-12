package ru.yandex.practicum.filmorate.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    private User user;
    private String userToString;
    private Gson gson;
    private UserController userController;

    @BeforeEach
    public void beforeEach() {
        this.userController = new UserController();

        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .create();

        user = User.builder()
                .name("Иван")
                .email("test@example.com")
                .login("ivan")
                .birthday(LocalDate.of(1994, 10, 24))
                .build();
    }

    // ТЕСТЫ РЕАЛИЗОВАНЫ ДОПОЛЬНИТЕЛЬНО ЧЕРЕЗ MockMvc ТАМ, ГДЕ ЭТО ВОЗМОЖНО
    // (соответственно тесты покрывают не только исключения, но и 4хх ошибки)

    // VALIDATOR TESTS
   @Test
   public void testValidatorEmptyEmail() {
        user.setEmail("");

        try {
            userController.userBasicValidation(user);
        } catch (ValidationException e) {
           assertEquals("Произошла ошибка при обновлении пользователя (пустой email)", e.getMessage());
       }
   }

    @Test
    public void testValidatorIncorrectEmail() {
        user.setEmail("sdfsdf..asdsdf.rew");
        try {
            userController.userBasicValidation(user);
        } catch (ValidationException e) {
            assertEquals("Произошла ошибка при обновлении пользователя (некорректный email)", e.getMessage());
        }    }

    @Test
    public void testValidatorEmptyLogin() {
        user.setLogin("");
        try {
            userController.userBasicValidation(user);
        } catch (ValidationException e) {
            assertEquals("Произошла ошибка при обновлении пользователя (пустой логин)", e.getMessage());
        }    }

    @Test
    public void testValidatorIncorrectLogin() {
        user.setLogin("AB  CD");
        try {
            userController.userBasicValidation(user);
        } catch (ValidationException e) {
            assertEquals("Произошла ошибка при обновлении пользователя (логин содержит пробелы)", e.getMessage());
        }    }

    @Test
    public void testValidatorIncorrectBD() {
        user.setBirthday(LocalDate.of(2132, 2, 12));
        try {
            userController.userBasicValidation(user);
        } catch (ValidationException e) {
            assertEquals("Произошла ошибка при обновлении пользователя (день рождения в будущем)", e.getMessage());
        }    }

    // API TESTS
    @Test
    @DisplayName("Создание пользователя")
    public void mustCreateUser() throws Exception {
        userToString = gson.toJson(user);

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .content(userToString)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Иван"));
    }

    @Test
    @DisplayName("Не должно создавать пользователя при пустом email'e")
    public void mustNotCreateOnEmptyEmail() throws Exception {
        user.setEmail("");
        userToString = gson.toJson(user);

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .content(userToString)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    @DisplayName("Не должно создавать пользователя при некорректном email'e")
    public void mustNotCreateOnNonValidEmail() throws Exception {
        user.setEmail("fksj??&^$%^%$.sdfsdf@asdsdf.rew");
        userToString = gson.toJson(user);

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .content(userToString)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    @DisplayName("Не должно создавать пользователя при пустом логине")
    public void mustNotCreateOnEmptyLogin() throws Exception {
        user.setLogin("");
        userToString = gson.toJson(user);

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .content(userToString)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    @DisplayName("Не должно создавать пользователя при некорректном логине")
    public void mustNotCreateOnNonValidLogin() throws Exception {
        user.setLogin("~~xxxNA giBATORxxx~~");
        userToString = gson.toJson(user);

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .content(userToString)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    @DisplayName("Не должно создавать пользователя с ДР в будущем")
    public void mustNotCreateOnBDinFuture() throws Exception {
        user.setBirthday(LocalDate.of(2123, 2,23));
        userToString = gson.toJson(user);

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .content(userToString)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }
}