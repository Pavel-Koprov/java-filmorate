package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;
import java.util.Collection;
import java.util.Optional;

public interface UserStorage {

    Collection<User> findAll();

    User create(User newUser);

    User update(User newUser);

    User getUserById(long userId);

    Optional<User> findUserById(long userId);
}
