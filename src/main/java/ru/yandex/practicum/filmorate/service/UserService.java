package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public User createUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            log.info("У пользователя с идентификатором {} пустое имя", user.getId());
            user = user.toBuilder().name(user.getLogin()).build();
        }
        return userStorage.addUser(user);
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

    public boolean addFriend(final Optional<Integer> id, final Optional<Integer> friendIdOptional) {
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
        return userStorage.addFriend(userId, friendId);
    }

    public boolean deleteFriend(final Optional<Integer> id, final Optional<Integer> friendIdOptional) {
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
        return userStorage.deleteFriend(userId, friendId);
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
        return userStorage.getFriendsById(userId);
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
        return userStorage.getMutualFriendsById(userId, otherUserId);
    }
}
