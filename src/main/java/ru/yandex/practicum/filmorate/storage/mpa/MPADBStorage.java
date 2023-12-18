package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.MPA;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Repository
@Primary
@Qualifier("MPADBStorage")
public class MPADBStorage implements MPAStorage {
    private final JdbcTemplate jdbcTemplate;
    private String sql;

    @Override
    public List<MPA> getAllMPAs() {
        sql = "SELECT id, name, description FROM mpa";
        return jdbcTemplate.query(sql, getMPAMapper());
    }

    @Override
    public MPA getMPAById(Integer id) {
        if (!findMPAById(id)) {
            log.warn("MPA с идентификатором {} не существует!", id);
            throw new NotFoundException("MPA с идентификатором " + id + " не существует!");
        }
        sql = "SELECT id, name, description FROM mpa " +
                "WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, getMPAMapper(), id);
    }

    private boolean findMPAById(Integer id) {
        sql = "SELECT id FROM mpa WHERE id = ?";
        SqlRowSet filmRaws = jdbcTemplate.queryForRowSet(sql, id);
        return filmRaws.next();
    }

    private static RowMapper<MPA> getMPAMapper() {
        return (rs, rowNum) -> MPA.builder()
                .id(rs.getInt("id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .build();
    }
}
