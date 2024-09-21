package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationExceptions;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final Map<Integer, User> users = new HashMap<>();
    private int userId = 0;

    @GetMapping
    public Collection<User> findAll() {
        log.info("Выполнение запроса на получение всех пользователей");
        return users.values();
    }

    @PostMapping
    public User create(@Valid @RequestBody User newUser) {
        log.info("Выполнение запроса на создание пользователя " + newUser.getLogin());
        userValidation(newUser);
        newUser.setId(getNextId());
        if (newUser.getName() == null) {
            newUser.setName(newUser.getLogin());
        }
        users.put(newUser.getId(), newUser);
        return newUser;
    }

    @PutMapping
    public User update(@Valid @RequestBody User newUser) {
        log.info("Выполнение запроса на обновление пользователя " + newUser.getLogin());
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

    private int getNextId() {
        return ++userId;
    }

    private void userValidation(User newUser) {
        if (newUser.getEmail() == null || newUser.getEmail().isEmpty()) {
            log.error("Пользователь не указал адрес эл.почты");
            throw new ValidationExceptions("Адрес эл.почты не указан");
        }
        if (!newUser.getEmail().contains("@")) {
            log.error("Пользователь указал некорректный адрес эл.почты");
            throw new ValidationExceptions("Адрес эл.почты не содержит @");
        }
        if (newUser.getLogin().isEmpty() || newUser.getLogin().contains(" ")) {
            log.error("Пользователь указал некорректный логин");
            throw new ValidationExceptions("Логин не может быть пустым и/или содержать пробелы");
        }
        if (newUser.getBirthday().isAfter(LocalDate.now())) {
            log.error("Пользователь указал некорректную дату рождения");
            throw new ValidationExceptions("Дата рождения не может быть в будущем");
        }
    }
}