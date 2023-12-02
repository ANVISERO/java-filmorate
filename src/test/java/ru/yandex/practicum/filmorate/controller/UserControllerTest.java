package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
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
public class UserControllerTest {
    private static UserController userController;
    private User user;
    private Validator validator;

    @BeforeEach
    void setUp() {
        userController = new UserController();
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void addUser() {
        user = User.builder()
                .email("1@gmail.com")
                .login("Логин")
                .name("Имя")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();

        userController.createUser(user.toBuilder().build());

        assertEquals(1, userController.getAllUsers().size(), "Неверное количество пользователей");
        assertEquals(user, userController.getAllUsers().get(0), "Пользователь добавлен некорректно");
    }

    @Test
    void updateUser() {
        user = User.builder()
                .email("1@gmail.com")
                .login("Логин")
                .name("Имя")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();

        userController.createUser(user.toBuilder().build());

        User user1 = User.builder()
                .id(1)
                .email("new1@gmail.com")
                .login("Новый логин")
                .name("Новое Имя")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();

        userController.updateUser(user1.toBuilder().build());

        assertEquals(1, userController.getAllUsers().size(), "Неверное количество пользователей");
        assertEquals(user1, userController.getAllUsers().get(0), "Пользователь обновлён некорректно");
    }

    @Test
    void addUserWithId() {
        user = User.builder()
                .id(1)
                .email("1@gmail.com")
                .login("Логин")
                .name("Имя")
                .birthday(LocalDate.of(2000, 1, 1))
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
        user = User.builder()
                .email("1@gmail.com")
                .login("Логин")
                .name("Имя")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();

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
        user = User.builder()
                .email("")
                .login("Логин")
                .name("Имя")
                .birthday(LocalDate.of(2000, 1, 1))
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
        user = User.builder()
                .email("1gmail.com")
                .login("Логин")
                .name("Имя")
                .birthday(LocalDate.of(2000, 1, 1))
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
        user = User.builder()
                .email("1@gmail.com")
                .login("")
                .name("Имя")
                .birthday(LocalDate.of(2000, 1, 1))
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
        user = User.builder()
                .email("1@gmail.com")
                .login("Неправильный логин")
                .name("Имя")
                .birthday(LocalDate.of(2000, 1, 1))
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
        user = User.builder()
                .email("1@gmail.com")
                .login("Логин")
                .name("Имя")
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
}
