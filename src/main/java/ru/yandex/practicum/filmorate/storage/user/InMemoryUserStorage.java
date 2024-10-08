package ru.yandex.practicum.filmorate.storage.user;

import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationExceptions;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();
    private long userId = 0;

    @Override
    public Collection<User> findAll() {
        log.info("Выполнение запроса на получение всех пользователей");
        return users.values();
    }

    @Override
    public User create(User newUser) {
        log.info("Выполнение запроса на создание пользователя {}", newUser);
        userValidation(newUser);
        newUser.setId(getNextId());
        if (newUser.getName() == null) {
            newUser.setName(newUser.getLogin());
        }
        users.put(newUser.getId(), newUser);
        return newUser;
    }

    @Override
    public User update(User newUser) {
        log.info("Выполнение запроса на обновление пользователя {}", newUser);
        if (newUser.getId() == null) {
            log.error("Не указан id пользователя {}", newUser);
            throw new ValidationException("Id пользователя должен быть указан");
        }
        if (!users.containsKey(newUser.getId())) {
            log.error("Пользователь с id = {} не найден", newUser.getId());
            throw new NotFoundException(String.format("Пользователя с id = %d нет в базе", newUser.getId()));
        }
        userValidation(newUser);
        User updatedUser = users.get(newUser.getId());
        updatedUser.setEmail(newUser.getEmail());
        updatedUser.setLogin(newUser.getLogin());
        if (newUser.getName() == null) {
            updatedUser.setName(newUser.getLogin());
        } else {
            updatedUser.setName(newUser.getName());
        }
        updatedUser.setBirthday(newUser.getBirthday());
        return updatedUser;
    }

    @Override
    public User getUserById(long userId) {
        return users.get(userId);
    }

    @Override
    public Optional<User> findUserById(long userId) {
        log.info("Поступил запрос GET на получение данных о пользователе с id = {}", userId);
        return users.values()
                .stream()
                .filter(user -> user.getId() == userId)
                .findFirst();
    }

    private long getNextId() {
        return ++userId;
    }

    private void userValidation(User newUser) {
        if (newUser.getEmail() == null || newUser.getEmail().isBlank()) {
            log.error("Пользователь не указал адрес эл.почты");
            throw new ValidationExceptions("Адрес эл.почты не указан");
        }
        if (!newUser.getEmail().contains("@")) {
            log.error("Пользователь указал некорректный адрес эл.почты");
            throw new ValidationExceptions("Адрес эл.почты не содержит @");
        }
        if (newUser.getLogin().isBlank() || newUser.getLogin().contains(" ")) {
            log.error("Пользователь указал некорректный логин");
            throw new ValidationExceptions("Логин не может быть пустым и/или содержать пробелы");
        }
        if (newUser.getBirthday().isAfter(LocalDate.now())) {
            log.error("Пользователь указал некорректную дату рождения");
            throw new ValidationExceptions("Дата рождения не может быть в будущем");
        }
    }
}

