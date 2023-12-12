package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private int userId = 0;
    private final UserStorage userStorage;

    public User createUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            log.info("У пользователя с идентификатором {} пустое имя", user.getId());
            user = user.toBuilder().name(user.getLogin()).build();
        }
        return userStorage.addUser(user.toBuilder().id(generateUserId()).build());
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User getUserById(final Optional<Integer> id) {
        if (id.isEmpty()) {
            log.warn("Попытка получить пользователя с пустым уникальным идентификатором");
            throw new NotFoundException("Уникальный идентификатор пользователя не может быть пустым");
        }
        Integer userId = id.get();
        if (userId <= 0) {
            log.warn("Попытка получить пользователя с неположительным уникальным идентификатором");
            throw new NotFoundException(
                    "Уникальный идентификатор пользователя не может быть отрицательным или равным нулю");
        }
        return userStorage.getUserById(userId);
    }

    public void addFriend(final Optional<Integer> id, final Optional<Integer> friendIdOptional) {
        if (id.isEmpty()) {
            log.warn("Попытка запроса на дружбу от пользователя с пустым уникальным идентификатором");
            throw new NotFoundException("Уникальный идентификатор пользователя не может быть пустым");
        }
        Integer userId = id.get();
        if (userId <= 0) {
            log.warn("Попытка запроса на дружбу от пользователя с неположительным уникальным идентификатором");
            throw new NotFoundException("Уникальный идентификатор пользователя не может быть отрицательным " +
                    "или равным нулю");
        }
        if (friendIdOptional.isEmpty()) {
            log.warn("Попытка запроса на дружбу c пользователем, у которого пустой уникальный идентификатор");
            throw new NotFoundException("Уникальный идентификатор пользователя не может быть пустым");
        }
        Integer friendId = friendIdOptional.get();
        if (friendId <= 0) {
            log.warn("Попытка запроса на дружбу c пользователем, у которого неположительный уникальный идентификатор");
            throw new NotFoundException(
                    "Уникальный идентификатор пользователя не может быть отрицательным или равным нулю");
        }
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        if (!user.addFriend(friendId)) {
            log.warn("Попытка добавить в друзья пользователя повторно. " +
                    "У пользователя с id = {} уже есть в друзьях пользователь с id = {}", userId, friendId);
            throw new RuntimeException("Попытка добавить в друзья пользователя повторно. " +
                    "У пользователя с id = " + userId + " уже есть в друзьях пользователь с id = " + friendId);
        }
        if (!friend.addFriend(userId)) {
            log.warn("Попытка добавить в друзья пользователя повторно. " +
                    "У пользователя с id = {} уже есть в друзьях пользователь с id = {}", friendId, userId);
            throw new RuntimeException("Попытка добавить в друзья пользователя повторно. " +
                    "У пользователя с id = " + friendId + " уже есть в друзьях пользователь с id = " + userId);
        }
        userStorage.updateUser(user);
        userStorage.updateUser(friend);
    }

    public void deleteFriend(final Optional<Integer> id, final Optional<Integer> friendIdOptional) {
        if (id.isEmpty()) {
            log.warn("Попытка прекратить дружбу от пользователя с пустым уникальным идентификатором");
            throw new NotFoundException("Уникальный идентификатор пользователя не может быть пустым");
        }
        Integer userId = id.get();
        if (userId <= 0) {
            log.warn("Попытка прекратить дружбу от пользователя с неположительным уникальным идентификатором");
            throw new NotFoundException("Уникальный идентификатор пользователя не может быть отрицательным " +
                    "или равным нулю");
        }
        if (friendIdOptional.isEmpty()) {
            log.warn("Попытка прекратить дружбу c пользователем, у которого пустой уникальный идентификатор");
            throw new NotFoundException("Уникальный идентификатор пользователя не может быть пустым");
        }
        Integer friendId = friendIdOptional.get();
        if (friendId <= 0) {
            log.warn("Попытка прекратить дружбу c пользователем, у которого неположительный уникальный идентификатор");
            throw new NotFoundException(
                    "Уникальный идентификатор пользователя не может быть отрицательным или равным нулю");
        }
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        if (!user.deleteFriend(friendId)) {
            log.warn("Попытка удалить из друзей пользователя повторно. " +
                    "Пользователь с id = {} не дружит с пользователем с id = {}", userId, friendId);
            throw new RuntimeException("Попытка удалить из друзей пользователя повторно. " +
                    "Пользователь с id = " + userId + " не дружит с пользователем с id = " + friendId);
        }
        if (!friend.deleteFriend(userId)) {
            log.warn("Попытка удалить из друзей пользователя повторно. " +
                    "Пользователь с id = {} не дружит с пользователем с id = {}", friendId, userId);
            throw new RuntimeException("Попытка удалить из друзей пользователя повторно. " +
                    "Пользователь с id = " + friendId + " не дружит с пользователем с id = " + userId);
        }
    }

    public List<User> getFriendsById(final Optional<Integer> id) {
        if (id.isEmpty()) {
            log.warn("Пользователь с пустым уникальным идентификатором пытается получить список друзей");
            throw new NotFoundException("Уникальный идентификатор пользователя не может быть пустым");
        }
        Integer userId = id.get();
        if (userId <= 0) {
            log.warn("Пользователь с неположительным уникальным идентификатором пытается получить список друзей");
            throw new NotFoundException("Уникальный идентификатор пользователя не может быть отрицательным " +
                    "или равным нулю");
        }
        List<User> users = new ArrayList<>();
        User user = userStorage.getUserById(userId);
        log.info("{} {}", user, UserService.class);
        for (Integer i : user.getFriends()) {
            users.add(userStorage.getUserById(i));
        }
        log.info("{} {}", users, UserService.class);
        return users;
    }

    public List<User> getMutualFriendsById(final Optional<Integer> id, final Optional<Integer> otherId) {
        if (id.isEmpty()) {
            log.warn("Попытка получить общих друзей от пользователя с пустым уникальным идентификатором");
            throw new NotFoundException("Уникальный идентификатор пользователя не может быть пустым");
        }
        Integer userId = id.get();
        if (userId <= 0) {
            log.warn("Попытка получить общих друзей от пользователя с неположительным уникальным идентификатором");
            throw new NotFoundException("Уникальный идентификатор пользователя не может быть отрицательным " +
                    "или равным нулю");
        }
        if (otherId.isEmpty()) {
            log.warn("Попытка получить общих друзей c пользователем, у которого пустой уникальный идентификатор");
            throw new NotFoundException("Уникальный идентификатор пользователя не может быть пустым");
        }
        Integer otherUserId = otherId.get();
        if (otherUserId <= 0) {
            log.warn("Попытка получить общих друзей c пользователем, " +
                    "у которого неположительный уникальный идентификатор");
            throw new NotFoundException(
                    "Уникальный идентификатор пользователя не может быть отрицательным или равным нулю");
        }
        Set<Integer> mutualIds = new HashSet<>(userStorage.getUserById(userId).getFriends());
        mutualIds.retainAll(userStorage.getUserById(otherUserId).getFriends());
        List<User> users = new ArrayList<>();
        for (Integer i : mutualIds) {
            users.add(userStorage.getUserById(i));
        }
        return users;
    }

    private int generateUserId() {
        return ++userId;
    }

}
