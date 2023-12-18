package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.validation.OnCreate;
import ru.yandex.practicum.filmorate.validation.OnUpdate;

import javax.validation.groups.Default;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    @PostMapping
    public Film addFilm(@Validated({OnCreate.class, Default.class}) @RequestBody Film film) {
        log.info("Пришёл запрос на добавление фильма {}", film);
        Film newFilm = filmService.addFilm(film);
        log.info("Добавление нового фильма прошло успешно. Добавленный фильм: {}", newFilm);
        return newFilm;
    }

    @PutMapping
    public Film updateFilm(@Validated({OnUpdate.class, Default.class}) @RequestBody Film film) {
        log.info("Пришёл запрос на обновление фильма {}", film);
        Film newFilm = filmService.updateFilm(film);
        log.info("Обновление фильма прошло успешно. Обновлённый фильм: {}", newFilm);
        return newFilm;
    }

    @GetMapping
    public List<Film> getAllFilms() {
        log.info("Пришёл запрос на получение всех фильмов");
        List<Film> films = filmService.getAllFilms();
        log.info("Получение всех фильмов прошло успешно. Получено фильмов: ({})", films.size());
        return films;
    }

    @GetMapping(path = "/{id}")
    public Film getFilmById(@PathVariable(name = "id") final Optional<Integer> id) {
        log.info("Пришёл запрос на получение фильма по уникальному идентификатору.");
        Film film = filmService.getFilmById(id);
        log.info("Получение фильма по уникальному идентификатору прошло успешно. Получен фильм: {}", film);
        return film;
    }

    @PutMapping(path = "/{id}/like/{userId}")
    public boolean addLike(
            @PathVariable(name = "id") final Optional<Integer> id,
            @PathVariable(name = "userId") final Optional<Integer> userId) {
        log.info("Пришёл запрос на добавление лайка фильму.");
        boolean isLike = filmService.addLike(id, userId);
        log.info("Добавление лайка фильму пошло успешно.");
        return isLike;
    }

    @DeleteMapping(path = "/{id}/like/{userId}")
    public boolean deleteLike(
            @PathVariable(name = "id") final Optional<Integer> id,
            @PathVariable(name = "userId") final Optional<Integer> userId) {
        log.info("Пришёл запрос на удаление лайка фильму.");
        boolean isLikeDeleted = filmService.deleteLike(id, userId);
        log.info("Удаление лайка фильму пошло успешно.");
        return isLikeDeleted;
    }

    @GetMapping(path = "/popular")
    public List<Film> getFilmsByCount(
            @RequestParam(name = "count", required = false, defaultValue = "10") final Integer count) {
        log.info("Пришёл запрос на список из первых {} фильмов по количеству лайков.", count);
        List<Film> films = filmService.getFilmsByCount(count);
        log.info("Получение списка из первых {} фильмов по количеству лайков прошло успешно.", count);
        return films;
    }
}
