package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Repository
@Primary
@Qualifier("FilmDBStorage")
public class FilmDBStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final GenreStorage genreStorage;
    private final UserStorage userStorage;
    private String sql;

    @Override
    public Film addFilm(Film film) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("id");
        int id = insert.executeAndReturnKey(filmToMap(film)).intValue();
        if (!film.getGenres().isEmpty()) {
            log.info("Обновление жанров");
            genreStorage.updateFilmGenres(id, new HashSet<>(film.getGenres()));
        }
        return film.toBuilder().id(id).build();
    }

    @Override
    public Film updateFilm(Film film) {
        if (!findFilmById(film.getId())) {
            log.warn("Фильм с идентификатором {} не существует!", film.getId());
            throw new NotFoundException("Фильм с идентификатором " + film.getId() + " не существует!");
        }
        sql = "UPDATE films " +
                "SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? " +
                "WHERE id = ?";
        jdbcTemplate.update(sql, film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration(), film.getMpa().getId(), film.getId());
        if (!film.getGenres().isEmpty()) {
            genreStorage.updateFilmGenres(film.getId(), new HashSet<>(film.getGenres()));
            return film.toBuilder().genres(new ArrayList<>(new HashSet<>(film.getGenres()))
                    .stream().sorted(Comparator.comparing(Genre::getId)).collect(Collectors.toList())).build();
        }
        genreStorage.deleteFilmGenres(film.getId());
        return film;
    }

    @Override
    public List<Film> getAllFilms() {
        sql = "SELECT f.*, m.name " +
                "FROM films AS f " +
                "LEFT JOIN mpa AS m ON f.mpa_id = m.id";
        return jdbcTemplate.query(sql, getFilmMapper());
    }

    @Override
    public Film getFilmById(Integer id) {
        if (!findFilmById(id)) {
            log.warn("Фильм с идентификатором {} не существует!", id);
            throw new NotFoundException("Фильм с идентификатором " + id + " не существует!");
        }
        sql = "SELECT f.*, m.name " +
                "FROM films AS f " +
                "LEFT JOIN mpa AS m ON f.mpa_id = m.id " +
                "WHERE f.id = ?";
        return jdbcTemplate.queryForObject(sql, getFilmMapper(), id);
    }

    @Override
    public void deleteStorage() {
        sql = "DELETE FROM films";
        jdbcTemplate.update(sql);
    }

    @Override
    public boolean addLike(Integer filmId, Integer userId) {
        if (!findFilmById(filmId)) {
            log.warn("Попытка получить фильм с несуществующим идентификатором id = {}", filmId);
            throw new NotFoundException("Фильм с идентификатором id = " + filmId + " не существует");
        }
        if (!userStorage.findUserById(userId)) {
            log.warn("Попытка получить пользователя с несуществующим идентификатором id = {}", userId);
            throw new NotFoundException("Пользователь с идентификатором id = " + userId + " не существует");
        }
        sql = "MERGE INTO likes (film_id, user_id) " +
                "KEY (film_id, user_id) " +
                "VALUES (?, ?)";
        return jdbcTemplate.update(sql, filmId, userId) > 0;
    }

    @Override
    public boolean deleteLike(Integer filmId, Integer userId) {
        if (!findFilmById(filmId)) {
            log.warn("Попытка получить фильм с несуществующим идентификатором id = {}", filmId);
            throw new NotFoundException("Фильм с идентификатором id = " + filmId + " не существует");
        }
        if (!userStorage.findUserById(userId)) {
            log.warn("Попытка получить пользователя с несуществующим идентификатором id = {}", userId);
            throw new NotFoundException("Пользователь с идентификатором id = " + userId + " не существует");
        }
        sql = "DELETE FROM likes " +
                "WHERE film_id = ? AND user_id = ?";
        return jdbcTemplate.update(sql, filmId, userId) > 0;
    }

    @Override
    public List<Film> getFilmsByCount(Integer count) {
        sql = "SELECT f.*, m.name, COUNT(l.user_id) AS amount_of_likes " +
                "FROM films AS f " +
                "LEFT JOIN likes AS l ON f.id = l.film_id " +
                "LEFT JOIN mpa AS m ON f.mpa_id = m.id " +
                "GROUP BY f.name " +
                "ORDER BY amount_of_likes DESC " +
                "LIMIT ?";
        return jdbcTemplate.query(sql, getFilmMapper(), count);
    }


    private boolean findFilmById(Integer id) {
        sql = "SELECT id FROM films WHERE id = ?";
        SqlRowSet filmRaws = jdbcTemplate.queryForRowSet(sql, id);
        return filmRaws.next();
    }

    private RowMapper<Film> getFilmMapper() {
        return (rs, rowNum) -> {
            Film film = Film.builder()
                    .id(rs.getInt("id"))
                    .name(rs.getString("name"))
                    .description(rs.getString("description"))
                    .releaseDate(rs.getDate("release_date").toLocalDate())
                    .duration(rs.getInt("duration"))
                    .mpa(MPA.builder().id(rs.getInt("films.mpa_id"))
                            .name(rs.getString("mpa.name")).build())
                    .build();
            List<Genre> genres = genreStorage.getGenresByFilmId(film.getId())
                    .stream().sorted(Comparator.comparing(Genre::getId)).collect(Collectors.toList());
            return film.toBuilder().genres(genres).build();
        };
    }

    private static Map<String, Object> filmToMap(Film film) {
        return Map.of(
                "name", film.getName(),
                "description", film.getDescription(),
                "release_date", film.getReleaseDate(),
                "duration", film.getDuration(),
                "mpa_id", film.getMpa().getId());
    }
}
