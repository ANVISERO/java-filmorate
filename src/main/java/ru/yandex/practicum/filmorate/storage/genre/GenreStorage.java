package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Set;

public interface GenreStorage {
    List<Genre> getAllGenres();

    Genre getGenreById(Integer id);

    List<Genre> getGenresByFilmId(Integer filmId);

    void updateFilmGenres(Integer filmId, Set<Genre> genres);

    void deleteFilmGenres(Integer filmId);

    void createFilmGenres(Integer filmId, Set<Genre> genres);

}
