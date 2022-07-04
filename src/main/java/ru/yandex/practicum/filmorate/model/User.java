package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
@Builder
public class User {
    private int id;

    @NotBlank(message = "Поле email не может быть пустым")
    @Email(message = "Email пользователя (->xxx<-@mail.com) может включать латинские буквы (a-z), цифры (0-9) и точку (.).",
            regexp = "^[a-z0-9.]+\\@[a-z0-9]+.[a-z]+")
    private String email;

    @NotBlank(message = "Поле логин не может быть пустым")
    @Pattern(message = "Логин пользователя может включать латинские буквы (a-z) (A-Z), цифры (0-9) и точку (.)",
            regexp = "[a-zA-Z\\d.]+")
    private  String login;
    private String name;

    @Past(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;
}
