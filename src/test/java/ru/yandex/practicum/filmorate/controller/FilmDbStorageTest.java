package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.validation.OnCreate;
import ru.yandex.practicum.filmorate.validation.OnUpdate;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidationException;
import javax.validation.Validator;
import javax.validation.groups.Default;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmDbStorageTest {
    @Qualifier("FilmDBStorage")
    private final FilmStorage filmStorage;
    private Film film;
    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
        init();
    }

    @Test
    @Order(1)
    void addFilm() {
        filmStorage.addFilm(film);

        assertEquals(1, filmStorage.getAllFilms().size(), "Неверное количество фильмов");
        assertEquals(1, filmStorage.getAllFilms().get(0).getId(), "Фильм добавлен некорректно");
        assertEquals("G", filmStorage.getFilmById(1).getMpa().getName(), "Фильм добавлен некорректно");
    }

    @Test
    @Order(2)
    void updateFilm() {
        Film film1 = film.toBuilder()
                .id(1)
                .name("Новое название")
                .description("Новое описание")
                .build();

        filmStorage.updateFilm(film1.toBuilder().build());

        assertEquals(1, filmStorage.getAllFilms().size(), "Неверное количество фильмов");
        assertEquals("Новое название", filmStorage.getAllFilms().get(0).getName(), "Фильм обновлён некорректно");
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
                .mpa(MPA.builder().id(1).build())
                .build();
    }
}
