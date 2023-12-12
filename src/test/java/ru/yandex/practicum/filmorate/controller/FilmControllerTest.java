package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.validation.OnCreate;
import ru.yandex.practicum.filmorate.validation.OnUpdate;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.groups.Default;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class FilmControllerTest {
    private FilmController filmController;
    private FilmStorage filmStorage;
    private Film film;
    private Validator validator;

    @BeforeEach
    void setUp() {
        filmStorage = new InMemoryFilmStorage();
        filmController = new FilmController(new FilmService(filmStorage));
        validator = Validation.buildDefaultValidatorFactory().getValidator();
        init();
    }

    @Test
    void addFilm() {
        filmController.addFilm(film.toBuilder().build());

        assertEquals(1, filmStorage.getAllFilms().size(), "Неверное количество фильмов");
        assertEquals(film, filmStorage.getAllFilms().get(0), "Фильм добавлен некорректно");
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

        assertEquals(1, filmStorage.getAllFilms().size(), "Неверное количество фильмов");
        assertEquals(film1, filmStorage.getAllFilms().get(0), "Фильм обновлён некорректно");
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

    @Test
    void addLike() {
        Film film1 = film.toBuilder().id(1).build();
        filmStorage.addFilm(film1);

        filmController.addLike(Optional.of(film1.getId()), Optional.of(1));

        assertEquals(1, filmStorage.getFilmById(film1.getId()).getLikes().size(),
                "Количество лайков не совпадает с ожидаемым.");
        assertEquals(1, new ArrayList<>(filmStorage.getFilmById(film1.getId()).getLikes()).get(0));
    }

    @Test
    void deleteLike() {
        Film film1 = film.toBuilder().id(1).build();
        filmStorage.addFilm(film1);

        filmController.addLike(Optional.of(film1.getId()), Optional.of(1));
        filmController.deleteLike(Optional.of(film1.getId()), Optional.of(1));

        assertEquals(0, filmStorage.getFilmById(film1.getId()).getLikes().size(),
                "Количество лайков не совпадает с ожидаемым.");
    }

    @Test
    void getFilmsByCount() {
        Film film1 = film.toBuilder().id(1).build();
        filmStorage.addFilm(film1);

        Film film2 = film.toBuilder().id(2).build();
        filmStorage.addFilm(film2);

        filmController.addLike(Optional.of(film1.getId()), Optional.of(1));
        filmController.addLike(Optional.of(film1.getId()), Optional.of(2));
        filmController.addLike(Optional.of(film1.getId()), Optional.of(3));
        filmController.addLike(Optional.of(film2.getId()), Optional.of(1));

        assertEquals(film1, filmController.getFilmsByCount(1).get(0));
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
