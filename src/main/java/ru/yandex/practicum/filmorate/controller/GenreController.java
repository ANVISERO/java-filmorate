package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/genres")
public class GenreController {
    private final GenreService genreService;

    @GetMapping
    public List<Genre> getAllGenres() {
        log.info("Пришёл запрос на получение всех жанров");
        List<Genre> genres = genreService.getAllGenres();
        log.info("Получение всех жанров прошло успешно. Получено жанров: ({})", genres.size());
        return genres;
    }

    @GetMapping(path = "/{id}")
    public Genre getGenreById(@PathVariable(name = "id") final Optional<Integer> id) {
        log.info("Пришёл запрос на получение жанра по уникальному идентификатору.");
        Genre genre = genreService.getGenreById(id);
        log.info("Получение жанра по уникальному идентификатору прошло успешно. Получен жанр: {}", genre);
        return genre;
    }
}
