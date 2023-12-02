package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validation.OnCreate;
import ru.yandex.practicum.filmorate.validation.OnUpdate;

import javax.validation.ValidationException;
import javax.validation.groups.Default;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class UserController {
    private static Map<Integer, User> users = new HashMap<>();
    private int userId = 0;

    @PostMapping("/user")
    @Validated(OnCreate.class)
    public User createUser(@Validated({OnCreate.class, Default.class}) @RequestBody User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            log.info("У пользователя с идентификатором {} пустое имя", user.getId());
            user = user.toBuilder().name(user.getLogin()).build();
        }
        User newUser = user.toBuilder().id(generateUserId()).build();
        users.put(newUser.getId(), newUser);
        log.info("Добавление нового пользователя {}", newUser);
        return newUser;
    }

    @PutMapping("/user")
    @Validated(OnUpdate.class)
    public User updateUser(@Validated({OnUpdate.class, Default.class}) @RequestBody User user) {
        if (!users.containsKey(user.getId())) {
            log.error("Пользователь с идентификатором {} не существует!", user.getId());
            throw new ValidationException("Пользователь с идентификатором " + user.getId() + " не существует!");
        }
        users.put(user.getId(), user);
        log.info("Обновление пользователя {}", user);
        return user;
    }

    @GetMapping("/users")
    public List<User> getAllUsers() {
        List<User> usersList = new ArrayList<>(users.values());
        log.info("Получение всех пользователей ({})", usersList.size());
        return usersList;
    }

    private int generateUserId() {
        return ++userId;
    }
}
