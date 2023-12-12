package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.ValidationException;
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
        log.info("Фильм добавлен в хранилище. {}", film);
        return film.toBuilder().build();
    }

    @Override
    public Film updateFilm(Film film) {
        if (!films.containsKey(film.getId())) {
            log.warn("Фильм с идентификатором {} не существует!", film.getId());
            throw new ValidationException("Фильм с идентификатором " + film.getId() + " не существует!");
        }
        films.put(film.getId(), film);
        log.info("Обновлённый фильм добавлен в хранилище. {}", film);
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
}
