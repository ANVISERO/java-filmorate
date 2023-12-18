package ru.yandex.practicum.filmorate.storage.user;


import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    User addUser(User user);

    User updateUser(User user);

    List<User> getAllUsers();

    User getUserById(Integer id);

    void deleteStorage();

    boolean findUserById(Integer id);

    boolean addFriend(Integer userId, Integer friendId);

    boolean deleteFriend(Integer userId, Integer friendId);

    List<User> getFriendsById(Integer userId);

    List<User> getMutualFriendsById(Integer userId, Integer otherUserId);
}
