package ru.practicum.exploreWithMe.mapper;

import ru.practicum.exploreWithMe.dto.UserDto;
import ru.practicum.exploreWithMe.dto.UserShortDto;
import ru.practicum.exploreWithMe.model.User;

public class UserMapper {
    public static UserDto toDto(User user) {
        return new UserDto(
                user.getId(),
                user.getEmail(),
                user.getName()
        );
    }

    public static User fromDto(UserDto userDto) {
        return new User(
                userDto.getId(),
                userDto.getEmail(),
                userDto.getName()
        );
    }

    public static UserShortDto toShortDto(User user) {
        return new UserShortDto(user.getId(), user.getName());
    }
}
