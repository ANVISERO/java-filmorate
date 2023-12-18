package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.validation.OnCreate;
import ru.yandex.practicum.filmorate.validation.OnUpdate;

import javax.validation.groups.Default;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    @Validated(OnCreate.class)
    public User createUser(@Validated({OnCreate.class, Default.class}) @RequestBody User user) {
        log.debug("Пришёл запрос на создание пользователя {}", user);
        User newUser = userService.createUser(user);
        log.debug("Добавление нового пользователя прошло успешно. Добавленный пользователь: {}", newUser);
        return newUser;
    }

    @PutMapping
    @Validated(OnUpdate.class)
    public User updateUser(@Validated({OnUpdate.class, Default.class}) @RequestBody User user) {
        log.debug("Пришёл запрос на обновление пользователя {}", user);
        User newUser = userService.updateUser(user);
        log.debug("Обновление нового пользователя прошло успешно. Обновлённый пользователь: {}", newUser);
        return newUser;
    }

    @GetMapping
    public List<User> getAllUsers() {
        log.debug("Пришёл запрос на получение всех пользователей.");
        List<User> users = userService.getAllUsers();
        log.debug("Получение всех пользователей прошло успешно. Получено пользователей: ({})", users.size());
        return users;
    }

    @GetMapping(path = "/{id}")
    public User getUserById(@PathVariable(name = "id") final Optional<Integer> id) {
        log.debug("Пришёл запрос на получение пользователя по его уникальному идентификатору.");
        User newUser = userService.getUserById(id);
        log.debug("Получение пользователя по его уникальному идентификатору прошло успешно. " +
                "Получен пользователь: {}", newUser);
        return newUser;
    }

    @PutMapping(path = "/{id}/friends/{friendId}")
    public boolean addFriend(
            @PathVariable(name = "id") final Optional<Integer> id,
            @PathVariable(name = "friendId") final Optional<Integer> friendId) {
        log.debug("Пришёл запрос на добавление в друзья пользователя");
        boolean isFriend = userService.addFriend(id, friendId);
        log.debug("Запрос на добавление в друзья пользователя прошёл успешно. " +
                "Теперь пользователи с уникальными идентификаторами {} и {} друзья", id.get(), friendId.get());
        return isFriend;
    }

    @DeleteMapping(path = "/{id}/friends/{friendId}")
    public boolean deleteFriend(
            @PathVariable(name = "id") final Optional<Integer> id,
            @PathVariable(name = "friendId") final Optional<Integer> friendId) {
        log.debug("Пришёл запрос на удаление из друзей пользователя");
        boolean isDeleted = userService.deleteFriend(id, friendId);
        log.debug("Запрос на удаление из друзей прошёл успешно. Теперь пользователи с уникальными идентификаторами" +
                " {} и {} не являются друзьями", id.get(), friendId.get());
        return isDeleted;
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriendsById(@PathVariable(name = "id") final Optional<Integer> id) {
        log.debug("Пришёл запрос на получение списка друзей пользователя");
        List<User> users = userService.getFriendsById(id);
        log.debug("Запрос на получение списка друзей пользователя прошёл успешно. " +
                "Получено друзей пользователя: ({})", users.size());
        return users;
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getMutualFriendsById(
            @PathVariable(name = "id") final Optional<Integer> id,
            @PathVariable(name = "otherId") final Optional<Integer> otherId) {
        log.debug("Пришёл запрос на получение списка друзей, общих с другим пользователем");
        List<User> users = userService.getMutualFriendsById(id, otherId);
        log.debug("Запрос на получение друзей, общих с другим пользователем прошёл успешно. " +
                "Получено общих друзей: ({})", users.size());
        return users;
    }
}
