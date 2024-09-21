package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationExceptions;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {
    private FilmController filmController;
    private Film film;

    @BeforeEach
    public void beforeEach() {
        filmController = new FilmController();
        film = Film.builder().name("name")
                .description("description")
                .releaseDate(LocalDate.now())
                .duration(105L)
                .build();
    }

    @Test
    void ifNameEmptyError() {
        film.setName("");
        assertThrows(ValidationExceptions.class, () -> filmController.create(film));
    }

    @Test
    void ifDescriptionMoreThan200Error() {
        film.setDescription("f".repeat(201));
        assertThrows(ValidationExceptions.class, () -> filmController.create(film));
    }

    @Test
    void ifReleaseDateBefore28121985Error() {
        film.setReleaseDate(LocalDate.of(1800,1,1));
        assertThrows(ValidationExceptions.class, () -> filmController.create(film));
    }

    @Test
    void ifDurationLessThan0Error() {
        film.setDuration(0L);
        assertThrows(ValidationExceptions.class, () -> filmController.create(film));
    }
}
