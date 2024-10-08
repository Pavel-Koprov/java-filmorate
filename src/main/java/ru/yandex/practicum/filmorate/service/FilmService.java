package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import java.util.*;

@Slf4j
@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film create(Film newFilm) {
        return filmStorage.create(newFilm);
    }

    public Film update(Film newFilm) {
        return filmStorage.update(newFilm);
    }

    public Optional<Film> findFilmById(long filmId) {
        return filmStorage.findFilmById(filmId);
    }

    public void addLike(long filmId, long userId) {
        log.info("Получен запрос PUT на добавление лайка к фильму с id = {} от пользователя с id = {}",
                filmId, userId);
        Film film;
        User user;
        Optional<Film> optFilm = filmStorage.findFilmById(filmId);
        if (optFilm.isPresent()) {
            film = optFilm.get();
        } else {
            log.error("Фильм с id = {} не найден", filmId);
            throw new NotFoundException(String.format("Фильма с id = %d нет в базе", filmId));
        }
        Optional<User> optUser = userStorage.findUserById(userId);
        if (optUser.isPresent()) {
            user = optUser.get();
        } else {
            log.error("Пользователь с id = {} не найден", userId);
            throw new NotFoundException(String.format("Пользователя с id = %d нет в базе", userId));
        }
        film.addLike(user.getId());
    }

    public void removeLike(long filmId, long userId) {
        log.info("Получен запрос DELETE на удаление лайка у фильма с id = {} от пользователя с id = {}",
                filmId, userId);
        Film film;
        Optional<Film> optFilm = filmStorage.findFilmById(filmId);
        if (optFilm.isPresent()) {
            film = optFilm.get();
        } else {
            log.error("Фильм с id = {} не был найден", filmId);
            throw new NotFoundException(String.format("Фильма с id = %d нет в базе", filmId));
        }
        if (film.getLikes().contains(userId)) {
            film.removeLike(userId);
        } else {
            log.error("Лайк пользователя с id = {} к фильму с id = {} не найден",
                    userId, filmId);
            throw new NotFoundException(String.format("Лайк пользователя с id = %d к фильму с id = %d не найден",
                    userId, filmId));
        }
    }

    public List<Film> getPopularFilms(int count) {
        log.info("Поступил запрос GET на получение {} наиболее популярных фильмов по количеству лайков", count);
        return filmStorage.findAll().stream()
                .filter((Film film) -> film.getLikes() != null)
                .sorted(Comparator.comparing((Film film) -> film.getLikes().size()).reversed())
                .limit(count)
                .toList();
    }
}
