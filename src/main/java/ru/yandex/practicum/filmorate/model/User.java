package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.NonFinal;
import ru.yandex.practicum.filmorate.validation.OnCreate;
import ru.yandex.practicum.filmorate.validation.OnUpdate;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Value
@Builder(toBuilder = true)
@EqualsAndHashCode(exclude = {"id", "friends"})
public class User {
    @Null(groups = OnCreate.class, message = "Уникальный идентификатор задаётся автоматически")
    @NotNull(groups = OnUpdate.class, message = "Укажите уникальный идентификатор пользователя")
    Integer id;
    @NotBlank(message = "Электронная почта не может быть пустой")
    @Email(message = "Электронная почта неправильного формата")
    String email;
    @NotBlank(message = "Логин не может быть пустым")
    @Pattern(regexp = "^\\S*$", message = "Логин не может содержать пробельных символов")
    String login;
    String name;
    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    LocalDate birthday;
    @JsonIgnore
    @NonFinal
    Set<Integer> friends;

    public boolean addFriend(Integer userId) {
        if (friends == null) {
            friends = new HashSet<>();
        }
        return friends.add(userId);
    }

    public boolean deleteFriend(Integer userId) {
        if (friends == null) {
            friends = new HashSet<>();
        }
        return friends.remove(userId);
    }

    public Set<Integer> getFriends() {
        if (friends == null) {
            return new HashSet<>();
        }
        return new HashSet<>(friends);
    }
}
