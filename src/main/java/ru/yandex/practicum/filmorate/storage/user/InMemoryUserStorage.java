package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private static Map<Integer, User> users = new HashMap<>();

    @Override
    public User addUser(User user) {
        if (users.containsKey(user.getId())) {
            log.warn("Попытка добавить пользователя с уже существующим идентификатором");
            throw new RuntimeException("Пользователь с id = " + user.getId() + " уже существует");
        }
        users.put(user.getId(), user);
        log.info("Пользователь добавлен в хранилище. {}", user);
        return user.toBuilder().build();
    }

    @Override
    public User updateUser(User user) {
        if (!users.containsKey(user.getId())) {
            log.warn("Пользователь с идентификатором {} не существует!", user.getId());
            throw new ValidationException("Фильм с идентификатором " + user.getId() + " не существует!");
        }
        log.info("{}", users);
        users.put(user.getId(), user);
        log.info("{}", users);
        log.info("Обновлённый пользователь добавлен в хранилище. {}", user);
        return user.toBuilder().build();
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUserById(Integer id) {
        if (!users.containsKey(id)) {
            log.warn("Пользователь с идентификатором {} не существует!", id);
            throw new NotFoundException("Пользователь с идентификатором " + id + " не существует!");
        }
        log.info("{} {}",users.get(id), InMemoryUserStorage.class);
        return users.get(id).toBuilder().build();
    }

    @Override
    public void deleteStorage() {
        users.clear();
    }
}
