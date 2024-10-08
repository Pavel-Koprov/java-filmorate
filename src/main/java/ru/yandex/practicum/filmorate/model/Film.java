package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class Film {
    private Integer id;
    @NotNull
    private String name;
    @NotNull
    private String description;
    @NotNull
    private LocalDate releaseDate;
    @NotNull
    private Long duration;
    Set<Long> likes;

    public void addLike(long userId) {
        if (likes == null) {
            likes = new HashSet<>();
        }
        likes.add(userId);
    }

    public void removeLike(long userId) {
        likes.remove(userId);
    }
}
