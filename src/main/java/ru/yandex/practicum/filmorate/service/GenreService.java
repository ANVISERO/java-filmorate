package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreStorage genreStorage;

    public List<Genre> getAllGenres() {
        return genreStorage.getAllGenres();
    }

    public Genre getGenreById(final Optional<Integer> id) {
        if (id.isEmpty()) {
            log.warn("Попытка получить жанр с пустым уникальным идентификатором");
            throw new NotFoundException("Уникальный идентификатор жанра не может быть пустым");
        }
        Integer genreId = id.get();
        if (genreId <= 0) {
            log.warn("Попытка получить жанр с неположительным уникальным идентификатором");
            throw new NotFoundException("Уникальный идентификатор жанра не может быть отрицательным или равным нулю");
        }
        return genreStorage.getGenreById(genreId);
    }
}
