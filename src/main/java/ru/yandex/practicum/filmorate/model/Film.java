package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

@Data
@Builder
public class Film {
    private int id;

    @NotBlank(message = "Имя не может быть пустым")
    private String name;

    @Length(message = "Ваше описание более 200 символов", max = 200)
    private String description;
    private LocalDate releaseDate;

    @Min(message = "Продолжительность фильма не может быть менее 1 минуты", value = 1)
    private double duration;
}
