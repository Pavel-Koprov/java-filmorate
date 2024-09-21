package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final HashMap<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        log.info("запрошен список пользователей");
        return users.values();
    }

    @PutMapping
    public User update(@RequestBody User newUser) {

        if (newUser == null) {
            log.error("пустой объект пользователя");
            return newUser;
        }

        if (newUser.getId() == null) {
            log.error("пользователь не ввёл Id");
            throw new ValidationException("Id должен быть указан");
        }

        if (users.containsKey(newUser.getId())) {
            User oldUser = users.get(newUser.getId());

            if (newUser.getEmail() == null || newUser.getEmail().isBlank() || !newUser.getEmail().contains("@")) {
                log.error("пользователь ввёл неверный email");
                throw new ValidationException("электронная почта не может быть пустой и должна содержать символ @");
            } else if (newUser.getLogin() == null || newUser.getLogin().isBlank() ||
                    !newUser.getLogin().contains(" ")) {
                log.error("пользователь ввёл неверный логин");
                throw new ValidationException("логин не может быть пустым и содержать пробелы");
            } else if (newUser.getBirthday() == null || newUser.getBirthday().isAfter(LocalDate.now())) {
                log.error("пользователь ввёл неверную дату рождения");
                throw new ValidationException("дата рождения не может быть в будущем");
            }

            oldUser.setEmail(newUser.getEmail());
            oldUser.setLogin(newUser.getLogin());
            oldUser.setBirthday(newUser.getBirthday());
            oldUser.setName(newUser.getName());

            if (newUser.getName() == null) {
                log.info("имя пользователя - его логин");
                oldUser.setName(oldUser.getLogin());
            }

            log.info("обновлены данные пользователя");

            return oldUser;
        }
        log.error("пользователь ввёл неверный Id");
        throw new ValidationException("Пользователь с id = " + newUser.getId() + " не найден");
    }

    @PostMapping
    public User create(@RequestBody User user) {

        if (user == null) {
            log.error("пустой объект пользователя");
            throw new ValidationException("нет данных пользователя");
        }

        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {

            if (users.values().stream()
                    .map(User::getEmail)
                    .anyMatch(email -> email.equals(user.getEmail()))) {
                log.error("пользователь ввёл уже используемый email");
                throw new ValidationException("электронная почта не должна быть уже использована" +
                        " другим пользователем");
            }
            log.error("пользователь ввёл неверный email");
            throw new ValidationException("электронная почта не может быть пустой и должна содержать символ @");
        } else if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.error("пользователь ввёл неверный логин");
            throw new ValidationException("логин не может быть пустым и содержать пробелы");
        } else if (user.getName() == null) {
            log.info("имя пользователя - его логин");
            user.setName(user.getLogin());
        } else if (user.getBirthday() == null || user.getBirthday().isAfter(LocalDate.now())) {
            log.error("пользователь ввёл неверную дату рождения");
            throw new ValidationException("дата рождения не может быть в будущем");
        }

        user.setId(getNextId());
        users.put(user.getId(), user);

        log.info("добавлен новый пользователь");

        return user;
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
