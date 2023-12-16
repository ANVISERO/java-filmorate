package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private int filmId = 0;
    private final FilmStorage filmStorage;

    public Film addFilm(Film film) {
        return filmStorage.addFilm(film.toBuilder().id(generateFilmId()).build());
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film getFilmById(final Optional<Integer> id) {
        if (id.isEmpty()) {
            log.warn("Попытка получить фильм с пустым уникальным идентификатором");
            throw new NotFoundException("Уникальный идентификатор фильма не может быть пустым");
        }
        Integer filmId = id.get();
        if (filmId <= 0) {
            log.warn("Попытка получить фильм с неположительным уникальным идентификатором");
            throw new NotFoundException("Уникальный идентификатор фильма не может быть отрицательным или равным нулю");
        }
        return filmStorage.getFilmById(filmId);
    }

    public Film addLike(final Optional<Integer> id, final Optional<Integer> userIdOptional) {
        if (id.isEmpty()) {
            log.warn("Попытка поставить лайк фильму с пустым уникальным идентификатором");
            throw new NotFoundException("Уникальный идентификатор фильма не может быть пустым");
        }
        Integer filmId = id.get();
        if (filmId <= 0) {
            log.warn("Попытка поставить лайк фильму с неположительным уникальным идентификатором");
            throw new NotFoundException("Уникальный идентификатор фильма не может быть отрицательным или равным нулю");
        }
        if (userIdOptional.isEmpty()) {
            log.warn("Попытка поставить лайк фильму с пустым уникальным идентификатором пользователя");
            throw new NotFoundException("Уникальный идентификатор пользователя не может быть пустым");
        }
        Integer userId = userIdOptional.get();
        if (userId <= 0) {
            log.warn("Попытка поставить лайк фильму с неположительным идентификатором пользователя");
            throw new NotFoundException(
                    "Уникальный идентификатор пользователя не может быть отрицательным или равным нулю");
        }
        Film film = filmStorage.getFilmById(filmId);
        if (!film.addLike(userId)) {
            log.warn("Попытка пользователя с id = {} поставить лайк фильма второй раз.", userId);
            throw new RuntimeException("Недопустимо ставить больше одного лайка фильму");
        }
        return filmStorage.updateFilm(film);
    }

    public Film deleteLike(final Optional<Integer> id, final Optional<Integer> userIdOptional) {
        if (id.isEmpty()) {
            log.warn("Попытка удалить лайк с фильма с пустым уникальным идентификатором");
            throw new NotFoundException("Уникальный идентификатор фильма не может быть пустым");
        }
        Integer filmId = id.get();
        if (filmId <= 0) {
            log.warn("Попытка удалить лайк с фильма с неположительным уникальным идентификатором");
            throw new NotFoundException("Уникальный идентификатор фильма не может быть отрицательным или равным нулю");
        }
        if (userIdOptional.isEmpty()) {
            log.warn("Попытка удалить лайк с фильма с пустым уникальным идентификатором пользователя");
            throw new NotFoundException("Уникальный идентификатор пользователя не может быть пустым");
        }
        Integer userId = userIdOptional.get();
        if (userId <= 0) {
            log.warn("Попытка удалить лайк с фильма с неположительным идентификатором пользователя");
            throw new NotFoundException(
                    "Уникальный идентификатор пользователя не может быть отрицательным или равным нулю");
        }
        Film film = filmStorage.getFilmById(filmId);
        if (!film.deleteLike(userId)) {
            log.warn("Попытка пользователя с id = {} удалить лайк с фильма, на котором нет лайков.", userId);
            throw new RuntimeException("Недопустимо удалять лайк с фильма, на котором нет лайков.");
        }
        return filmStorage.updateFilm(film);
    }

    public List<Film> getFilmsByCount(Integer count) {
        if (count <= 0) {
            log.warn("Пользователь ввёл отрицательное количество фильмов.");
            throw new NotFoundException("Количество фильмов не может быть отрицательным.");
        }
        List<Film> films = filmStorage.getAllFilms();
        if (films.size() < count) {
            count = films.size();
        }
        return films.stream()
                .sorted(Comparator.comparing((film) -> -1 * film.getLikes().size()))
                .limit(count).collect(Collectors.toList());
    }

    private int generateFilmId() {
        return ++filmId;
    }
}
