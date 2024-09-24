package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationExceptions;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;

class UserControllerTest {
    private UserController userController;
    private User user;

    @BeforeEach
    public void beforeEach() {
        userController = new UserController();
        user = User.builder()
                .email("blank@email.ru")
                .login("login")
                .name("name")
                .birthday(LocalDate.now())
                .build();
    }

    @Test
    void ifNotContainsDogError() {
        user.setEmail("blankemail.ru");
        assertThrows(ValidationExceptions.class, () -> userController.create(user));
    }

    @Test
    void ifEmptyEmailError() {
        user.setEmail("");
        assertThrows(ValidationExceptions.class, () -> userController.create(user));
    }

    @Test
    void ifEmptyLoginError() {
        user.setLogin("");
        assertThrows(ValidationExceptions.class, () -> userController.create(user));
    }

    @Test
    void ifContainsSpaceLoginError() {
        user.setLogin("login login");
        assertThrows(ValidationExceptions.class, () -> userController.create(user));
    }

    @Test
    void ifBirthdayAfterNowError() {
        user.setBirthday(LocalDate.now().plusDays(1));
        assertThrows(ValidationExceptions.class, () -> userController.create(user));
    }
}