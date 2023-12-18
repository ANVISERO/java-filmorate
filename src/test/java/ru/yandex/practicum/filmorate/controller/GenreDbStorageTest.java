package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GenreDbStorageTest {
    @Qualifier("GenreDBStorage")
    private final GenreStorage genreStorage;

    @Test
    public void findGenreById() {
        Genre genre = genreStorage.getGenreById(1);
        assertThat(genre).isNotNull();
        assertEquals(1, genre.getId());
        assertEquals("Комедия", genre.getName());
    }

    @Test
    public void getNotFoundGenre() {
        NotFoundException ex = assertThrows(NotFoundException.class, () -> genreStorage.getGenreById(9999));
        assertEquals("Жанр с идентификатором 9999 не существует!", ex.getMessage());
    }

    @Test
    public void getAllGenres() {
        List<Genre> genres = genreStorage.getAllGenres();
        assertThat(genres).isNotNull();
        assertEquals(6, genres.size());
        assertEquals("Комедия", genres.get(0).getName());
        assertEquals("Триллер", genres.get(3).getName());
    }
}
