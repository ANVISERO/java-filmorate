package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.validation.OnCreate;
import ru.yandex.practicum.filmorate.validation.OnUpdate;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.groups.Default;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserDbStorageTest {
    @Qualifier("UserDBStorage")
    private final UserStorage userStorage;
    private User user;
    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
        init();
    }

    @Test
    @Order(1)
    void addUser() {
        userStorage.addUser(user);

        assertEquals(1, userStorage.getAllUsers().size(), "Неверное количество пользователей");
        assertEquals(1, userStorage.getUserById(1).getId(), "Пользователь добавлен некорректно");
        assertEquals(user, userStorage.getAllUsers().get(0), "Пользователь добавлен некорректно");
    }

    @Test
    @Order(2)
    void updateUser() {
        User user2 = user.toBuilder()
                .id(1)
                .login("Новый логин")
                .name("Новое Имя")
                .build();

        userStorage.updateUser(user2.toBuilder().build());

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

    private void init() {
        user = User.builder()
                .email("1@gmail.com")
                .login("Логин")
                .name("Имя")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
    }
}
