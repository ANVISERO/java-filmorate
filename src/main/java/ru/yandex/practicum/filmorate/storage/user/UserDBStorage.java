package ru.yandex.practicum.filmorate.storage.user;

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
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Map;


@Slf4j
@RequiredArgsConstructor
@Repository
@Primary
@Qualifier("UserDBStorage")
public class UserDBStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
    private String sql;

    @Override
    public User addUser(User user) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");
        int id = insert.executeAndReturnKey(userToMap(user)).intValue();
        return user.toBuilder().id(id).build();
    }

    @Override
    public User updateUser(User user) {
        if (!findUserById(user.getId())) {
            log.warn("Пользователь с идентификатором {} не существует!", user.getId());
            throw new NotFoundException("Пользователь с идентификатором " + user.getId() + " не существует!");
        }
        sql = "UPDATE users " +
                "SET email = ?, login = ?, name = ?, birthday = ? " +
                "WHERE id = ?";
        if (jdbcTemplate.update(sql, user.getEmail(), user.getLogin(), user.getName(),
                user.getBirthday(), user.getId()) == 0) {
            log.warn("Пользователь с идентификатором {} не существует!", user.getId());
            throw new NotFoundException("Пользователь с идентификатором " + user.getId() + " не существует!");
        }
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        sql = "SELECT id, email, login, name, birthday FROM users";
        return jdbcTemplate.query(sql, getUserMapper());
    }

    @Override
    public User getUserById(Integer id) {
        if (!findUserById(id)) {
            log.warn("Пользователь с идентификатором {} не существует!", id);
            throw new NotFoundException("Пользователь с идентификатором " + id + " не существует!");
        }
        sql = "SELECT id, email, login, name, birthday " +
                "FROM users " +
                "WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, getUserMapper(), id);
    }

    @Override
    public void deleteStorage() {
        sql = "TRUNCATE TABLE users";
        jdbcTemplate.update(sql);
    }

    @Override
    public boolean findUserById(Integer id) {
        sql = "SELECT id FROM users WHERE id = ?";
        SqlRowSet userRaws = jdbcTemplate.queryForRowSet(sql, id);
        return userRaws.next();
    }

    @Override
    public boolean addFriend(Integer userId, Integer friendId) {
        if (!findUserById(userId)) {
            log.warn("Пользователь с идентификатором {} не существует!", userId);
            throw new NotFoundException("Пользователь с идентификатором " + userId + " не существует!");
        }
        if (!findUserById(friendId)) {
            log.warn("Пользователь с идентификатором {} не существует!", friendId);
            throw new NotFoundException("Пользователь с идентификатором " + friendId + " не существует!");
        }
        sql = "MERGE INTO friendship (user_id, another_user_id)" +
                "KEY(user_id, another_user_id)" +
                "VALUES (?, ?)";
        return jdbcTemplate.update(sql, userId, friendId) > 0;
    }

    @Override
    public boolean deleteFriend(Integer userId, Integer friendId) {
        if (!findUserById(userId)) {
            log.warn("Пользователь с идентификатором {} не существует!", userId);
            throw new NotFoundException("Пользователь с идентификатором " + userId + " не существует!");
        }
        if (!findUserById(friendId)) {
            log.warn("Пользователь с идентификатором {} не существует!", friendId);
            throw new NotFoundException("Пользователь с идентификатором " + friendId + " не существует!");
        }
        sql = "DELETE friendship " +
                "WHERE user_id = ? AND another_user_id = ?";
        return jdbcTemplate.update(sql, userId, friendId) > 0;
    }

    @Override
    public List<User> getFriendsById(Integer userId) {
        if (!findUserById(userId)) {
            log.warn("Пользователь с идентификатором {} не существует!", userId);
            throw new NotFoundException("Пользователь с идентификатором " + userId + " не существует!");
        }
        sql = "SELECT u.id, u.email, u.login, u.name, u.birthday " +
                "FROM friendship AS f " +
                "JOIN users AS u ON f.another_user_id = u.id " +
                "WHERE user_id = ?";
        return jdbcTemplate.query(sql, getUserMapper(), userId);
    }

    @Override
    public List<User> getMutualFriendsById(Integer userId, Integer otherUserId) {
        if (!findUserById(userId)) {
            log.warn("Пользователь с идентификатором {} не существует!", userId);
            throw new NotFoundException("Пользователь с идентификатором " + userId + " не существует!");
        }
        if (!findUserById(otherUserId)) {
            log.warn("Пользователь с идентификатором {} не существует!", otherUserId);
            throw new NotFoundException("Пользователь с идентификатором " + otherUserId + " не существует!");
        }
        sql = "SELECT u.id, u.email, u.login, u.name, u.birthday " +
                "FROM friendship f1 " +
                "JOIN friendship f2 ON f1.another_user_id = f2.another_user_id " +
                "JOIN users AS u ON f1.another_user_id = u.id " +
                "WHERE f1.user_id = ? AND f2.user_id = ?";
        return jdbcTemplate.query(sql, getUserMapper(), userId, otherUserId);
    }

    private static RowMapper<User> getUserMapper() {
        return (rs, rowNum) -> User.builder()
                .id(rs.getInt("id"))
                .email(rs.getString("email"))
                .login(rs.getString("login"))
                .name(rs.getString("name"))
                .birthday(rs.getDate("birthday").toLocalDate())
                .build();
    }

    private static Map<String, Object> userToMap(User user) {
        return Map.of(
                "email", user.getEmail(),
                "login", user.getLogin(),
                "name", user.getName(),
                "birthday", user.getBirthday());
    }
}
