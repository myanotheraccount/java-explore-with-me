package ru.practicum.exploreWithMe.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exploreWithMe.dto.UserDto;
import ru.practicum.exploreWithMe.exception.ConflictException;
import ru.practicum.exploreWithMe.exception.ValidationException;
import ru.practicum.exploreWithMe.mapper.UserMapper;
import ru.practicum.exploreWithMe.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<UserDto> getAll(List<Long> ids) {
        log.info("получение списка пользователей");
        return userRepository.findAllById(ids).stream().map(UserMapper::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserDto saveUser(UserDto userDto) {
        if (userRepository.existsByName(userDto.getName())) {
            throw new ConflictException("такое имя пользователя уже существует");
        }

        try {
            log.info(String.format("добавление пользователя %s", userDto));
            return UserMapper.toDto(userRepository.save(UserMapper.fromDto(userDto)));
        } catch (Exception e) {
            throw new ValidationException("не удалось сохранить пользователя");
        }
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        log.info(String.format("удаление пользователя %d", id));
        userRepository.deleteById(id);
    }
}
