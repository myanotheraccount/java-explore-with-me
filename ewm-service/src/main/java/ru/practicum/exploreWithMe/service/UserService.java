package ru.practicum.exploreWithMe.service;

import ru.practicum.exploreWithMe.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getAll(List<Long> ids);

    UserDto saveUser(UserDto userDto);

    void deleteUser(Long id);
}
