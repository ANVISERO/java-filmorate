package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
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
@RequestMapping("/films")
public class FilmController {
    private static final Map<Integer, Film> films = new HashMap<>();
    private int filmId = 0;

    @PostMapping
    public Film addFilm(@Validated({OnCreate.class, Default.class}) @RequestBody Film film) {
        Film newFilm = film.toBuilder().id(generateFilmId()).build();
        films.put(newFilm.getId(), newFilm);
        log.info("Добавление нового фильма {}", newFilm);
        return newFilm;
    }

    @PutMapping
    public Film updateFilm(@Validated({OnUpdate.class, Default.class}) @RequestBody Film film) {
        if (!films.containsKey(film.getId())) {
            log.error("Фильм с идентификатором {} не существует!", film.getId());
            throw new ValidationException("Фильм с идентификатором " + film.getId() + " не существует!");
        }
        films.put(film.getId(), film);
        log.info("Обновление фильма {}", film);
        return film;
    }

    @GetMapping
    public List<Film> getAllFilms() {
        List<Film> filmsList = new ArrayList<>(films.values());
        log.info("Получение всех фильмов ({})", filmsList.size());
        return filmsList;
    }

    private int generateFilmId() {
        return ++filmId;
    }
}
