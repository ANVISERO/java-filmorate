package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private static final Map<Integer, Film> films = new HashMap<>();

    @Override
    public Film addFilm(Film film) {
        films.put(film.getId(), film);
        log.debug("Фильм добавлен в хранилище. {}", film);
        return film.toBuilder().build();
    }

    @Override
    public Film updateFilm(Film film) {
        if (!films.containsKey(film.getId())) {
            log.warn("Фильм с идентификатором {} не существует!", film.getId());
            throw new NotFoundException("Фильм с идентификатором " + film.getId() + " не существует!");
        }
        films.put(film.getId(), film);
        log.debug("Обновлённый фильм добавлен в хранилище. {}", film);
        return film.toBuilder().build();
    }

    @Override
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film getFilmById(Integer id) {
        if (!films.containsKey(id)) {
            log.warn("Фильм с идентификатором {} не существует!", id);
            throw new NotFoundException("Фильм с идентификатором " + id + " не существует!");
        }
        return films.get(id).toBuilder().build();
    }

    @Override
    public void deleteStorage() {
        films.clear();
    }

    @Override
    public boolean addLike(Integer filmId, Integer userId) {
        return false;
    }

    @Override
    public boolean deleteLike(Integer filmId, Integer userId) {
        return false;
    }

    @Override
    public List<Film> getFilmsByCount(Integer count) {
        return null;
    }
}
