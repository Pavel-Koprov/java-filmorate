package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationExceptions;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private final Map<Integer, Film> films = new HashMap<>();
    private int filmId = 0;
    private static final LocalDate MOVIE_BIRTHDAY = LocalDate.of(1895, 12, 28);
    private static final Long MAX_DESCRIPTION_LENGTH = 200L;

    @GetMapping
    public Collection<Film> findAll() {
        log.info("Выполнение запроса на получение всех фильмов");
        return films.values();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film newFilm) {
        log.info("Выполнение запроса на создание нового фильма {}", newFilm);
        filmValidation(newFilm);
        newFilm.setId(getNextId());
        films.put(newFilm.getId(), newFilm);
        return newFilm;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film newFilm) {
        log.info("Выполнение запроса на обновление фильма {}", newFilm);
        filmValidation(newFilm);
        Film film = films.get(newFilm.getId());
        film.setName(newFilm.getName());
        film.setDescription(newFilm.getDescription());
        film.setReleaseDate(newFilm.getReleaseDate());
        film.setDuration(newFilm.getDuration());
        return film;
    }

    private int getNextId() {
        return ++filmId;
    }

    private void filmValidation(Film newFilm) {
        if (newFilm.getName() == null || newFilm.getName().isBlank()) {
            log.error("Пользователь попытался создать новый фильм с пустым названием");
            throw new ValidationExceptions("Название фильма не должно быть пустым");
        }
        if (newFilm.getDescription().isBlank() || newFilm.getDescription().length() > MAX_DESCRIPTION_LENGTH) {
            log.error("Пользователь попытался создать новый фильм с пустым описанием или длинной более 200 символов");
            throw new ValidationExceptions("Введенное описание должно быть не более 200 символов");
        }
        if (newFilm.getReleaseDate() == null || newFilm.getReleaseDate().isBefore(MOVIE_BIRTHDAY)) {
            log.error("Пользователь попытался создать новый фильм с датой релиза раньше 28.12.1895 года");
            throw new ValidationExceptions("Дата выxода фильма не может быть раньше 28.12.1895 года");
        }
        if (newFilm.getDuration() <= 0) {
            log.error("Пользователь попытался создать новый фильм с длительностью меньше 1 минуты");
            throw new ValidationExceptions("Длительность не может быть меньше 1 минуты");
        }
    }

}
