package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.validation.OnCreate;
import ru.yandex.practicum.filmorate.validation.OnUpdate;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.groups.Default;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class UserControllerTest {
    private UserStorage userStorage;
    private UserController userController;
    private User user;
    private Validator validator;

    @BeforeEach
    void setUp() {
        userStorage = new InMemoryUserStorage();
        userController = new UserController(new UserService(userStorage));
        validator = Validation.buildDefaultValidatorFactory().getValidator();
        init();
    }

    @Test
    void addUser() {
        userController.createUser(user.toBuilder().build());

        assertEquals(1, userStorage.getAllUsers().size(), "Неверное количество пользователей");
        assertEquals(user, userStorage.getAllUsers().get(0), "Пользователь добавлен некорректно");
    }

    @Test
    void updateUser() {
        User user1 = user.toBuilder().id(1).build();
        userStorage.addUser(user1);

        User user2 = user.toBuilder()
                .id(1)
                .login("Новый логин")
                .name("Новое Имя")
                .build();

        userController.updateUser(user2.toBuilder().build());

        assertEquals(1, userStorage.getAllUsers().size(), "Неверное количество пользователей");
        assertEquals(user2, userStorage.getAllUsers().get(0), "Пользователь обновлён некорректно");
    }

    @Test
    void addUserWithId() {
        user = user.toBuilder()
                .id(1)
                .build();

        ValidationException validationException = assertThrows(ValidationException.class, () -> {
            Set<ConstraintViolation<User>> constraintViolations;
            constraintViolations = validator.validate(user, OnCreate.class, Default.class);
            if (!constraintViolations.isEmpty()) {
                throw new ValidationException("Уникальный идентификатор задаётся автоматически");
            }
        });
        assertEquals("Уникальный идентификатор задаётся автоматически", validationException.getMessage(),
                "Некорректная валидация уникального идентификатора пользователя при добавлении");
    }

    @Test
    void updateUserWithoutId() {
        ValidationException validationException = assertThrows(ValidationException.class, () -> {
            Set<ConstraintViolation<User>> constraintViolations;
            constraintViolations = validator.validate(user, OnUpdate.class, Default.class);
            if (!constraintViolations.isEmpty()) {
                throw new ValidationException("Укажите уникальный идентификатор пользователя");
            }
        });
        assertEquals("Укажите уникальный идентификатор пользователя", validationException.getMessage(),
                "Некорректная валидация уникального идентификатора пользователя при обновлении");
    }

    @Test
    void validateUserWithEmptyEmail() {
        user = user.toBuilder()
                .email("")
                .build();

        ValidationException validationException = assertThrows(ValidationException.class, () -> {
            Set<ConstraintViolation<User>> constraintViolations;
            constraintViolations = validator.validate(user);
            if (!constraintViolations.isEmpty()) {
                throw new ValidationException("Электронная почта не может быть пустой");
            }
        });
        assertEquals("Электронная почта не может быть пустой", validationException.getMessage(),
                "Некорректная валидация электронной почты пользователя");
    }

    @Test
    void validateUserWithIncorrectEmail() {
        user = user.toBuilder()
                .email("1gmail.com")
                .build();

        ValidationException validationException = assertThrows(ValidationException.class, () -> {
            Set<ConstraintViolation<User>> constraintViolations;
            constraintViolations = validator.validate(user);
            if (!constraintViolations.isEmpty()) {
                throw new ValidationException("Электронная почта неправильного формата");
            }
        });
        assertEquals("Электронная почта неправильного формата", validationException.getMessage(),
                "Некорректная валидация электронной почты пользователя");
    }

    @Test
    void validateUserWithEmptyLogin() {
        user = user.toBuilder()
                .login("")
                .build();

        ValidationException validationException = assertThrows(ValidationException.class, () -> {
            Set<ConstraintViolation<User>> constraintViolations;
            constraintViolations = validator.validate(user);
            if (!constraintViolations.isEmpty()) {
                throw new ValidationException("Логин не может быть пустым");
            }
        });
        assertEquals("Логин не может быть пустым", validationException.getMessage(),
                "Некорректная валидация логина пользователя");
    }

    @Test
    void validateUserWithIncorrectLogin() {
        user = user.toBuilder()
                .login("Неправильный логин")
                .build();

        ValidationException validationException = assertThrows(ValidationException.class, () -> {
            Set<ConstraintViolation<User>> constraintViolations;
            constraintViolations = validator.validate(user);
            if (!constraintViolations.isEmpty()) {
                throw new ValidationException("Логин не может содержать пробельных символов");
            }
        });
        assertEquals("Логин не может содержать пробельных символов", validationException.getMessage(),
                "Некорректная валидация логина пользователя");
    }

    @Test
    void validateUserWithIncorrectBirthday() {
        user = user.toBuilder()
                .birthday(LocalDate.of(2030, 1, 1))
                .build();

        ValidationException validationException = assertThrows(ValidationException.class, () -> {
            Set<ConstraintViolation<User>> constraintViolations;
            constraintViolations = validator.validate(user);
            if (!constraintViolations.isEmpty()) {
                throw new ValidationException("Дата рождения не может быть в будущем");
            }
        });
        assertEquals("Дата рождения не может быть в будущем", validationException.getMessage(),
                "Некорректная валидация даты рождения пользователя");
    }

    @Test
    void addFriend() {
        User user1 = user.toBuilder().id(1).build();
        userStorage.addUser(user1);
        User user2 = user.toBuilder().id(2).build();
        userStorage.addUser(user2);

        userController.addFriend(Optional.of(user1.getId()), Optional.of(user2.getId()));

        assertEquals(1, userStorage.getUserById(user1.getId()).getFriends().size(),
                "Количество друзей не совпадает с ожидаемым.");
        assertEquals(1, userStorage.getUserById(user2.getId()).getFriends().size(),
                "Количество друзей не совпадает с ожидаемым.");
        assertEquals(1, new ArrayList<>(userStorage.getUserById(user2.getId()).getFriends()).get(0));
        assertEquals(2, new ArrayList<>(userStorage.getUserById(user1.getId()).getFriends()).get(0));
    }

    @Test
    void deleteFriend() {
        User user1 = user.toBuilder().id(1).build();
        userStorage.addUser(user1);
        User user2 = user.toBuilder().id(2).build();
        userStorage.addUser(user2);

        userController.addFriend(Optional.of(user1.getId()), Optional.of(user2.getId()));
        userController.deleteFriend(Optional.of(user1.getId()), Optional.of(user2.getId()));

        assertEquals(0, userStorage.getUserById(user1.getId()).getFriends().size(),
                "Количество друзей не совпадает с ожидаемым.");
        assertEquals(0, userStorage.getUserById(user2.getId()).getFriends().size(),
                "Количество друзей не совпадает с ожидаемым.");
    }

    @Test
    void getFriendsById() {
        User user1 = user.toBuilder().id(1).build();
        userStorage.addUser(user1);
        User user2 = user.toBuilder().id(2).build();
        userStorage.addUser(user2);

        userController.addFriend(Optional.of(user1.getId()), Optional.of(user2.getId()));
        List<User> friends = userController.getFriendsById(Optional.of(user1.getId()));

        assertEquals(1, friends.size());
        assertEquals(user2, friends.get(0));
    }

    @Test
    void getMutualFriendsById() {
        User user1 = user.toBuilder().id(1).build();
        userStorage.addUser(user1);
        User user2 = user.toBuilder().id(2).build();
        userStorage.addUser(user2);
        User user3 = user.toBuilder().id(3).build();
        userStorage.addUser(user3);

        userController.addFriend(Optional.of(user1.getId()), Optional.of(user2.getId()));
        userController.addFriend(Optional.of(user1.getId()), Optional.of(user3.getId()));
        userController.addFriend(Optional.of(user2.getId()), Optional.of(user3.getId()));
        List<User> friends = userController.getMutualFriendsById(Optional.of(user1.getId()), Optional.of(user2.getId()));

        assertEquals(1, friends.size());
        assertEquals(user3, friends.get(0));
    }

    private void init() {
        user = User.builder()
                .email("1@gmail.com")
                .login("Логин")
                .name("Имя")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
    }
}
