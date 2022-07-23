package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.bind.DefaultValue;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Data
@Builder
public class Film {
    private int id;
    @Min(message = "Рейтинг не может быть меньше 0", value = 0)
    @Value("${rate:0}")
    private long rate;
    private Set<Integer> rates;
    private List<String> genre;
    private Enum<Mpa> mpaEnum;

    @NotBlank(message = "Имя не может быть пустым")
    private String name;

    @Length(message = "Ваше описание более 200 символов", max = 200)
    private String description;
    private LocalDate releaseDate;

    @Min(message = "Продолжительность фильма не может быть менее 1 минуты", value = 1)
    private double duration;
}
