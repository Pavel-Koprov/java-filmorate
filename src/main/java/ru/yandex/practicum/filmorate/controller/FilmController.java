package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private final HashMap<Long,Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> findAll() {
        log.info("запрошен список фильмов");
        return films.values();
    }

    @PutMapping
    public Film update(@RequestBody Film newFilm) {

        if (newFilm == null) {
            log.error("пустой объект фильма");
            throw new ValidationException("нет обновлённых данных фильма");
        }

        if (newFilm.getId() == null) {
            log.error("пользователь не ввёл Id фильма");
            throw new ValidationException("Id должен быть указан");
        }

        if (films.containsKey(newFilm.getId())) {
            Film oldFilm = films.get(newFilm.getId());

            if (newFilm.getName() == null || newFilm.getName().isBlank()) {
                log.error("пользователь не ввёл название фильма");
                throw new ValidationException("Название фильма не может быть пустым");
            } else if (newFilm.getDescription() == null || newFilm.getDescription().length() > 200) {
                log.error("пользователь ввёл описание фильма больше 200 символов");
                throw new ValidationException("Максимальная длина описания — 200 символов");
            } else if (newFilm.getReleaseDate() == null ||
                    newFilm.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
                log.error("пользователь ввёл дату фильма раньше 28 декабря 1895 года");
                throw new ValidationException("дата релиза — не раньше 28 декабря 1895 года");
            } else if (newFilm.getDuration() <= 0) {
                log.error("пользователь ввёл отрицательную продолжительность фильма");
                throw new ValidationException("продолжительность фильма должна быть положительным числом");
            }

            oldFilm.setName(newFilm.getName());
            oldFilm.setDescription(newFilm.getDescription());
            oldFilm.setReleaseDate(newFilm.getReleaseDate());
            oldFilm.setDuration(newFilm.getDuration());

            log.info("обновлены данные фильма");

            return oldFilm;
        }
        log.error("пользователь ввёл неверный Id фильма");
        throw new ValidationException("Фильм с id = " + newFilm.getId() + " не найден");
    }

    @PostMapping
    public Film create(@RequestBody Film film) {

        if (film == null) {
            log.error("пустой объект фильма");
            throw new ValidationException("нет данных фильма");
        }

        if (film.getName() == null || film.getName().isBlank()) {
            log.error("пользователь не ввёл название фильма");
            throw new ValidationException("Название фильма не может быть пустым");
        } else if (film.getDescription() == null || film.getDescription().length() > 200) {
            log.error("пользователь ввёл описание фильма больше 200 символов");
            throw new ValidationException("Максимальная длина описания — 200 символов");
        } else if (film.getReleaseDate() == null ||
                film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.error("пользователь ввёл дату фильма раньше 28 декабря 1895 года");
            throw new ValidationException("дата релиза — не раньше 28 декабря 1895 года");
        } else if (film.getDuration() <= 0) {
            log.error("пользователь ввёл отрицательную продолжительность фильма");
            throw new ValidationException("продолжительность фильма должна быть положительным числом");
        }

        film.setId(getNextId());
        films.put(film.getId(), film);

        log.info("добавлен новый фильм");

        return film;
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
