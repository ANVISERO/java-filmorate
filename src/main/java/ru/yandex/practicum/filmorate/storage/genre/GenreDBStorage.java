package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Repository
@Primary
@Qualifier("GenreDBStorage")
public class GenreDBStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;
    private String sql;

    @Override
    public List<Genre> getAllGenres() {
        sql = "SELECT id, name FROM genres";
        return jdbcTemplate.query(sql, getGenreMapper());
    }

    @Override
    public Genre getGenreById(Integer id) {
        if (!findGenreById(id)) {
            log.warn("Жанр с идентификатором {} не существует!", id);
            throw new NotFoundException("Жанр с идентификатором " + id + " не существует!");
        }
        sql = "SELECT id, name FROM genres " +
                "WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, getGenreMapper(), id);
    }

    @Override
    public List<Genre> getGenresByFilmId(Integer filmId) {
        sql = "SELECT g.id, g.name " +
                "FROM genres AS g " +
                "JOIN film_genre AS fg ON g.id = fg.genre_id " +
                "WHERE fg.film_id = ?";
        return jdbcTemplate.query(sql, getGenreMapper(), filmId);
    }

    @Override
    public void updateFilmGenres(Integer filmId, Set<Genre> genres) {
        deleteFilmGenres(filmId);
        createFilmGenres(filmId, genres);
    }

    @Override
    public void deleteFilmGenres(Integer filmId) {
        sql = "DELETE FROM film_genre " +
                "WHERE film_id = ?";
        jdbcTemplate.update(sql, filmId);
        log.debug("Все жанры были удалены");
    }

    @Override
    public void createFilmGenres(Integer filmId, Set<Genre> genres) {
        List<Genre> genreList = new ArrayList<>(genres);
        sql = "INSERT INTO film_genre (film_id, genre_id) " +
                "VALUES (?, ?)";
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Genre genre = genreList.get(i);
                ps.setInt(1, filmId);
                ps.setInt(2, genre.getId());
            }

            @Override
            public int getBatchSize() {
                return genreList.size();
            }
        });
        log.debug("Все жанры фильма были записаны заново");
    }

    private boolean findGenreById(Integer id) {
        sql = "SELECT id FROM genres WHERE id = ?";
        SqlRowSet filmRaws = jdbcTemplate.queryForRowSet(sql, id);
        return filmRaws.next();
    }

    private static RowMapper<Genre> getGenreMapper() {
        return (rs, rowNum) -> Genre.builder()
                .id(rs.getInt("id"))
                .name(rs.getString("name"))
                .build();
    }
}
