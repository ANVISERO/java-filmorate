package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;

    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
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

    public boolean addLike(final Optional<Integer> id, final Optional<Integer> userIdOptional) {
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
        return filmStorage.addLike(filmId, userId);
    }

    public boolean deleteLike(final Optional<Integer> id, final Optional<Integer> userIdOptional) {
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
        return filmStorage.deleteLike(filmId, userId);
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
        return filmStorage.getFilmsByCount(count);
    }
}
