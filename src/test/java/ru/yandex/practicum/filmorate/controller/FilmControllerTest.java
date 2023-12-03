package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
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
public class FilmControllerTest {
    private static FilmController filmController;
    private Film film;
    private Validator validator;

    @BeforeEach
    void setUp() {
        filmController = new FilmController();
        validator = Validation.buildDefaultValidatorFactory().getValidator();
        init();
    }

    @Test
    void addFilm() {
        filmController.addFilm(film.toBuilder().build());

        assertEquals(1, filmController.getAllFilms().size(), "Неверное количество фильмов");
        assertEquals(film, filmController.getAllFilms().get(0), "Фильм добавлен некорректно");
    }

    @Test
    void updateFilm() {
        filmController.addFilm(film.toBuilder().build());

        Film film1 = film.toBuilder()
                .id(1)
                .name("Новое название")
                .description("Новое описание")
                .build();

        filmController.updateFilm(film1.toBuilder().build());

        assertEquals(1, filmController.getAllFilms().size(), "Неверное количество фильмов");
        assertEquals(film1, filmController.getAllFilms().get(0), "Фильм обновлён некорректно");
    }

    @Test
    void addFilmWithId() {
        film = film.toBuilder().id(1).build();

        ValidationException validationException = assertThrows(ValidationException.class, () -> {
            Set<ConstraintViolation<Film>> constraintViolations;
            constraintViolations = validator.validate(film, OnCreate.class, Default.class);
            if (!constraintViolations.isEmpty()) {
                throw new ValidationException("Уникальный идентификатор задаётся автоматически");
            }
        });
        assertEquals("Уникальный идентификатор задаётся автоматически", validationException.getMessage(),
                "Некорректная валидация уникального идентификатора фильма при добавлении");
    }

    @Test
    void updateFilmWithoutId() {
        ValidationException validationException = assertThrows(ValidationException.class, () -> {
            Set<ConstraintViolation<Film>> constraintViolations;
            constraintViolations = validator.validate(film, OnUpdate.class, Default.class);
            if (!constraintViolations.isEmpty()) {
                throw new ValidationException("Укажите уникальный идентификатор фильма");
            }
        });
        assertEquals("Укажите уникальный идентификатор фильма", validationException.getMessage(),
                "Некорректная валидация уникального идентификатора фильма при обновлении");
    }

    @Test
    void validateFilmWithEmptyName() {
        film = film.toBuilder().name(null).build();

        ValidationException validationException = assertThrows(ValidationException.class, () -> {
            Set<ConstraintViolation<Film>> constraintViolations;
            constraintViolations = validator.validate(film);
            if (!constraintViolations.isEmpty()) {
                throw new ValidationException("Название не может быть пустым");
            }
        });
        assertEquals("Название не может быть пустым", validationException.getMessage(),
                "Некорректная валидация названия фильма");
    }

    @Test
    void validateFilmWithDescriptionMoreThan200Symbols() {
        film = film.toBuilder()
                .description("Описание, очень очень очень очень очень очень очень очень очень очень очень очень " +
                        "очень очень очень очень очень очень очень очень очень очень очень очень очень очень очень " +
                        "очень очень очень очень очень очень очень очень очень очень очень очень длинное описание")
                .build();

        ValidationException validationException = assertThrows(ValidationException.class, () -> {
            Set<ConstraintViolation<Film>> constraintViolations;
            constraintViolations = validator.validate(film);
            if (!constraintViolations.isEmpty()) {
                throw new ValidationException("Описание фильма не должно содержать больше 200 символов");
            }
        });
        assertEquals("Описание фильма не должно содержать больше 200 символов",
                validationException.getMessage(), "Некорректная валидация описания фильма");
    }

    @Test
    void validateFilmWithIncorrectReleaseDate() {
        film = film.toBuilder()
                .releaseDate(LocalDate.of(1895, 1, 1))
                .build();

        ValidationException validationException = assertThrows(ValidationException.class, () -> {
            Set<ConstraintViolation<Film>> constraintViolations;
            constraintViolations = validator.validate(film, OnCreate.class, Default.class);
            if (!constraintViolations.isEmpty()) {
                throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года");
            }
        });
        assertEquals("Дата релиза — не раньше 28 декабря 1895 года", validationException.getMessage(),
                "Некорректная валидация даты релиза фильма");
    }

    @Test
    void validateFilmWithNegativeDuration() {
        film = film.toBuilder()
                .duration(-30)
                .build();

        ValidationException validationException = assertThrows(ValidationException.class, () -> {
            Set<ConstraintViolation<Film>> constraintViolations;
            constraintViolations = validator.validate(film, OnCreate.class, Default.class);
            if (!constraintViolations.isEmpty()) {
                throw new ValidationException("Продолжительность фильма должна быть положительной");
            }
        });
        assertEquals("Продолжительность фильма должна быть положительной", validationException.getMessage(),
                "Некорректная валидация продолжительности фильма");
    }

    private void init() {
        film = Film.builder()
                .name("Название")
                .description("Описание")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(30)
                .build();
    }
}
