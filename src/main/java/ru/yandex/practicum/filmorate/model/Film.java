package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import ru.yandex.practicum.filmorate.validation.OnCreate;
import ru.yandex.practicum.filmorate.validation.OnUpdate;
import ru.yandex.practicum.filmorate.validation.ReleaseDate;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Value
@Builder(toBuilder = true)
@EqualsAndHashCode(exclude = "id")
public class Film {
    @Null(groups = OnCreate.class, message = "Уникальный идентификатор задаётся автоматически")
    @NotNull(groups = OnUpdate.class, message = "Укажите уникальный идентификатор фильма")
    Integer id;
    @NotBlank(message = "Название фильма не может быть пустым")
    String name;
    @Size(max = 200, message = "Описание фильма не должно содержать больше 200 символов")
    String description;
    @ReleaseDate(message = "Дата релиза — не раньше 28 декабря 1895 года")
    LocalDate releaseDate;
    @Positive(message = "Продолжительность фильма должна быть положительной")
    Integer duration;
    @NotNull(message = "У фильма должен быть mpa")
    MPA mpa;
    List<Genre> genres;

    public List<Genre> getGenres() {
        if (genres == null) {
            return new ArrayList<>();
        }
        return genres;
    }
}
