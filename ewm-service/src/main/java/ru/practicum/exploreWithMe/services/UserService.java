package ru.practicum.exploreWithMe.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exploreWithMe.dto.UserDto;
import ru.practicum.exploreWithMe.exceptions.ConflictException;
import ru.practicum.exploreWithMe.exceptions.ValidationException;
import ru.practicum.exploreWithMe.mappers.UserMapper;
import ru.practicum.exploreWithMe.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserDto> getAll(List<Long> ids) {
        return userRepository.findAllById(ids).stream().map(UserMapper::toDto).collect(Collectors.toList());
    }

    @Transactional
    public UserDto saveUser(UserDto userDto) {
        if (userRepository.existsByName(userDto.getName())) {
            throw new ConflictException("такое имя пользователя уже существует");
        }

        try {
            return UserMapper.toDto(userRepository.save(UserMapper.fromDto(userDto)));
        } catch (Exception e) {
            throw new ValidationException("не удалось сохранить пользователя");
        }
    }

    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
