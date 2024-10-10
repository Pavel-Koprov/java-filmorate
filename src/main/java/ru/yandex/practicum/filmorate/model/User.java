package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class User {
    private Long id;
    @Email(message = "Введите адрес эл.почты в корректном формате")
    @NotBlank(message = "Адрес эл.почты не может быть пустым")
    private String email;
    @NotBlank(message = "Логин не может быть пустым")
    private String login;
    private String name;
    @Past(message = "Дата рождения не может быть в будущем")
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
