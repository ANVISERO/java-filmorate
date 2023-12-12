package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.validation.OnCreate;
import ru.yandex.practicum.filmorate.validation.OnUpdate;
import ru.yandex.practicum.filmorate.validation.ReleaseDate;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Value
@Builder(toBuilder = true)
@EqualsAndHashCode(exclude = {"id", "likes"})
public class Film {
    @Null(groups = OnCreate.class, message = "Уникальный идентификатор задаётся автоматически")
    @NotNull(groups = OnUpdate.class, message = "Укажите уникальный идентификатор фильма")
    Integer id;
    @NotBlank(message = "Название не может быть пустым")
    String name;
    @Size(max = 200, message = "Описание фильма не должно содержать больше 200 символов")
    String description;
    @ReleaseDate(message = "Дата релиза — не раньше 28 декабря 1895 года")
    LocalDate releaseDate;
    @Positive(message = "Продолжительность фильма должна быть положительной")
    Integer duration;
    @NonFinal
    @JsonIgnore
    Set<Integer> likes;

    public boolean addLike(Integer userId) {
        if (likes == null) {
            likes = new HashSet<>();
        }
        return likes.add(userId);
    }

    public boolean deleteLike(Integer userId) {
        if (likes == null) {
            likes = new HashSet<>();
        }
        return likes.remove(userId);
    }

    public Set<Integer> getLikes() {
        if (likes == null) {
            return new HashSet<>();
        }
        return likes;
    }
}
