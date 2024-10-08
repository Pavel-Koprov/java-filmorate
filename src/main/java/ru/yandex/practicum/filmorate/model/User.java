package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class User {
    private Long id;
    @NotNull
    @NotNull
    private String email;
    @NotNull
    private String login;
    private String name;
    @NotNull
    private LocalDate birthday;
    Set<Long> friends;

    public void addFriend(long userId) {
        if (friends == null) {
            friends = new HashSet<>();
        }
        friends.add(userId);
    }

    public void removeFriend(long userId) {
        friends.remove(userId);
    }
}
