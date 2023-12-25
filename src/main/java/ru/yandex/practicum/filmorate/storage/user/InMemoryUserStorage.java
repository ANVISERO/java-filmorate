package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@Qualifier(value = "InMemoryFilmStorage")
public class InMemoryUserStorage implements UserStorage {
    private static Map<Integer, User> users = new HashMap<>();

    @Override
    public User addUser(User user) {
        if (users.containsKey(user.getId())) {
            log.warn("Попытка добавить пользователя с уже существующим идентификатором");
            throw new RuntimeException("Пользователь с id = " + user.getId() + " уже существует");
        }
        users.put(user.getId(), user);
        log.debug("Пользователь добавлен в хранилище. {}", user);
        return user.toBuilder().build();
    }

    @Override
    public User updateUser(User user) {
        if (!users.containsKey(user.getId())) {
            log.warn("Пользователь с идентификатором {} не существует!", user.getId());
            throw new NotFoundException("Пользователь с идентификатором " + user.getId() + " не существует!");
        }
        users.put(user.getId(), user);
        log.debug("Обновлённый пользователь добавлен в хранилище. {}", user);
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
        log.debug("{} {}", users.get(id), InMemoryUserStorage.class);
        return users.get(id).toBuilder().build();
    }

    @Override
    public void deleteStorage() {
        users.clear();
    }

    @Override
    public boolean findUserById(Integer id) {
        return false;
    }

    @Override
    public boolean addFriend(Integer userId, Integer friendId) {
        return false;
    }

    @Override
    public boolean deleteFriend(Integer userId, Integer friendId) {
        return false;
    }

    @Override
    public List<User> getFriendsById(Integer userId) {
        return null;
    }

    @Override
    public List<User> getMutualFriendsById(Integer userId, Integer otherUserId) {
        return null;
    }
}
