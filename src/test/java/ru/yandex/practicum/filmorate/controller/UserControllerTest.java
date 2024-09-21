package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
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
        assertThrows(ValidationException.class, () -> userController.create(user));
    }

    @Test
    void ifEmptyEmailError() {
        user.setEmail("");
        assertThrows(ValidationException.class, () -> userController.create(user));
    }

    @Test
    void ifEmptyLoginError() {
        user.setLogin("");
        assertThrows(ValidationException.class, () -> userController.create(user));
    }

    @Test
    void ifContainsSpaceLoginError() {
        user.setLogin("login login");
        assertThrows(ValidationException.class, () -> userController.create(user));
    }

    @Test
    void ifBirthdayAfterNowError() {
        user.setBirthday(LocalDate.now().plusDays(1));
        assertThrows(ValidationException.class, () -> userController.create(user));
    }

}