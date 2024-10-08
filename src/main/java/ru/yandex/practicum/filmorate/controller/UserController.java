package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.*;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Collection<User> findAll() {
        return userService.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User create(@RequestBody User newUser) {
        return userService.create(newUser);
    }

    @PutMapping
    public User update(@RequestBody User newUser) {
        return userService.update(newUser);
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable("id") long id) {
        Optional<User> optUser = userService.findUserById(id);
        if (optUser.isPresent()) {
            return optUser.get();
        }
        throw new NotFoundException(String.format("Пользователь с id = %d не найден", id));
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable("id") long id,
                          @PathVariable("friendId") long friendId) {
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable("id") long id,
                             @PathVariable("friendId") long friendId) {
        userService.removeFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getAllFriends(@PathVariable("id") long id) {
        return userService.getAllFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable("id") long id,
                                       @PathVariable("otherId") long otherId) {
        return userService.getCommonFriends(id, otherId);
    }
}