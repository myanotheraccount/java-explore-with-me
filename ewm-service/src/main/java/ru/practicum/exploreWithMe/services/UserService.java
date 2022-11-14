package ru.practicum.exploreWithMe.services;

import org.springframework.stereotype.Service;
import ru.practicum.exploreWithMe.dto.UserDto;

@Service
public class UserService {
    public UserDto getUser(Long id) {
        return new UserDto();
    }
}
