package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import java.util.*;

@Slf4j
@Service
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public User create(User newUser) {
        return userStorage.create(newUser);
    }

    public User update(User newUser) {
        return userStorage.update(newUser);
    }

    public User findUserById(long userId) {
        Optional<User> optUser = userStorage.findUserById(userId);
        if (optUser.isPresent()) {
            return optUser.get();
        }
        throw new NotFoundException(String.format("Пользователь с id = %d не найден", userId));
    }

    public List<User> getAllFriends(long userId) {
        log.info("Поступил запрос GET на получение друзей пользователя {}", userId);
        Optional<User> optUser = userStorage.findUserById(userId);
        if (optUser.isPresent()) {
            User user = optUser.get();
            if (user.getFriends() == null) {
                return new ArrayList<>();
            } else {
                return user.getFriends()
                        .stream()
                        .map(userStorage::getUserById)
                        .toList();
            }
        } else {
            log.error("Пользователь с id={} не найден", userId);
            throw new NotFoundException(String.format("Пользователя с id = %d нет в базе", userId));
        }
    }

    public void addFriend(long userId, long friendId) {
        log.info("Получен запрос PUT на добавление пользователя c id = {} в друзья к пользователю с id = {}",
                userId, friendId);
        User user;
        User friend;
        Optional<User> optUser = userStorage.findUserById(userId);
        if (optUser.isPresent()) {
            user = optUser.get();
        } else {
            log.error("Пользователь с id = {} не найден", userId);
            throw new NotFoundException(String.format("Пользователя с id = %d нет в базе", userId));
        }
        Optional<User> optFriend = userStorage.findUserById(friendId);
        if (optFriend.isPresent()) {
            friend = optFriend.get();
        } else {
            log.error("Пользователь с id = {} не найден", friendId);
            throw new NotFoundException(String.format("Пользователя с id = %d нет в базе", friendId));
        }
        user.addFriend(friendId);
        friend.addFriend(userId);
    }

    public void removeFriend(long userId, long friendId) {
        log.info("Получен запрос DELETE на удаление пользователя c id = {} из друзей пользователя с id = {}",
                friendId, userId);
        User user;
        User friend;
        Optional<User> optUser = userStorage.findUserById(userId);
        if (optUser.isPresent()) {
            user = optUser.get();
        } else {
            log.error("Пользователь с id = {} не был найден", userId);
            throw new NotFoundException(String.format("Пользователя с id = %d нет в базе", userId));
        }
        Optional<User> optFriend = userStorage.findUserById(friendId);
        if (optFriend.isPresent()) {
            friend = optFriend.get();
        } else {
            log.error("Пользователь с id = {} не был найден", friendId);
            throw new NotFoundException(String.format("Пользователя с id = %d нет в базе", friendId));
        }
        if (user.getFriends() != null && friend.getFriends() != null) {
            if (user.getFriends().contains(friendId)) {
                user.removeFriend(friendId);
                friend.removeFriend(userId);
            }
        }
    }

    public List<User> getCommonFriends(Long userId, long friendId) {
        log.info("Поступил запрос GET на получение списка общих друзей пользователей c id ={} и id ={}",
                userId, friendId);
        User user;
        User friend;
        Optional<User> optUser = userStorage.findUserById(userId);
        if (optUser.isPresent()) {
            user = optUser.get();
        } else {
            log.error("Пользователя с id = {} нет в базе", userId);
            throw new NotFoundException(String.format("Пользователя с id = %d нет в базе", userId));
        }
        Optional<User> optFriend = userStorage.findUserById(friendId);
        if (optFriend.isPresent()) {
            friend = optFriend.get();
        } else {
            log.error("Пользователя с id = {} нет в базе", friendId);
            throw new NotFoundException(String.format("Пользователя с id = %d нет в базе", friendId));
        }
        Set<Long> userFriends = user.getFriends();
        Set<Long> friendFriends = friend.getFriends();
        if (userFriends != null && friendFriends != null) {
            boolean hasIntersection = userFriends.stream().anyMatch(friendFriends::contains);
            if (hasIntersection) {
                Set<Long> commonFriends = new HashSet<>(userFriends);
                commonFriends.retainAll(friendFriends);
                return commonFriends.stream()
                        .map(userStorage::getUserById)
                        .toList();
            }
        }
        return null;
    }
}
