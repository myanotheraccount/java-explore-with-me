package ru.practicum.exploreWithMe.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exploreWithMe.dto.CompilationDto;
import ru.practicum.exploreWithMe.dto.EventShortDto;
import ru.practicum.exploreWithMe.dto.NewCompilationDto;
import ru.practicum.exploreWithMe.exception.ValidationException;
import ru.practicum.exploreWithMe.mapper.CategoryMapper;
import ru.practicum.exploreWithMe.mapper.CompilationMapper;
import ru.practicum.exploreWithMe.mapper.EventMapper;
import ru.practicum.exploreWithMe.mapper.UserMapper;
import ru.practicum.exploreWithMe.model.Compilation;
import ru.practicum.exploreWithMe.model.CompilationsEvents;
import ru.practicum.exploreWithMe.repository.CategoryRepository;
import ru.practicum.exploreWithMe.repository.CompilationRepository;
import ru.practicum.exploreWithMe.repository.CompilationsEventsRepository;
import ru.practicum.exploreWithMe.repository.EventRepository;
import ru.practicum.exploreWithMe.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final CompilationsEventsRepository compilationsEventsRepository;
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public CompilationServiceImpl(CompilationRepository compilationRepository, CompilationsEventsRepository compilationsEventsRepository, EventRepository eventRepository, CategoryRepository categoryRepository, UserRepository userRepository) {
        this.compilationRepository = compilationRepository;
        this.compilationsEventsRepository = compilationsEventsRepository;
        this.eventRepository = eventRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    @Override
    public CompilationDto getById(Long id) {
        CompilationDto dto = CompilationMapper.toDto(compilationRepository.getReferenceById(id));
        List<Long> eventIds = compilationsEventsRepository.getAllByCompilation(id).stream()
                .map(CompilationsEvents::getEvent).collect(Collectors.toList());
        dto.setEvents(toEventShortDto(eventIds));
        log.info(String.format("получение подборки по id %d", id));
        return dto;
    }

    @Override
    @Transactional
    public CompilationDto addCompilation(NewCompilationDto newCompilationDto) {
        if (newCompilationDto.getTitle() == null) {
            throw new ValidationException("у подборки отсутсвует заголовок");
        }
        List<Long> eventsIds = newCompilationDto.getEvents();
        Compilation compilation = compilationRepository.save(CompilationMapper.fromNewDto(newCompilationDto));
        compilationsEventsRepository.saveAll(CompilationMapper.toCompilationEvents(compilation.getId(), eventsIds));

        CompilationDto dto = CompilationMapper.toDto(compilation);
        dto.setEvents(toEventShortDto(eventsIds));
        log.info(String.format("добавление подборки %s", newCompilationDto));
        return dto;
    }

    @Override
    public List<CompilationDto> getPinned(boolean pinned, Pageable pageable) {
        Page<Compilation> compilations = compilationRepository.findAllByPinned(pinned, pageable);
        log.info("получение закрепленных подборок");
        return compilations.stream().map(compilation -> {
            CompilationDto dto = CompilationMapper.toDto(compilation);
            List<Long> eventIds = compilationsEventsRepository.getAllByCompilation(compilation.getId()).stream()
                    .map(CompilationsEvents::getEvent).collect(Collectors.toList());
            dto.setEvents(toEventShortDto(eventIds));
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void addCompilationEvent(Long compId, Long eventId) {
        log.info(String.format("добавление события %d в подборку %d", eventId, compId));
        compilationsEventsRepository.save(new CompilationsEvents(compId, eventId));
    }

    @Override
    @Transactional
    public void pinById(Long compId, Boolean pinned) {
        Compilation compilation = compilationRepository.getReferenceById(compId);
        compilation.setPinned(pinned);
        log.info(String.format("изменение закрепления подборки %d %b", compId, pinned));
        compilationRepository.save(compilation);
    }

    @Override
    @Transactional
    public void deleteCompilation(Long compId) {
        log.info(String.format("удаление подборки %d", compId));
        compilationRepository.deleteById(compId);
    }

    @Override
    @Transactional
    public void deleteCompilationEvent(Long compId, Long eventId) {
        log.info(String.format("удаление события %d из подборки %d", eventId, compId));
        compilationsEventsRepository.delete(new CompilationsEvents(compId, eventId));
    }

    private List<EventShortDto> toEventShortDto(List<Long> eventsIds) {
        return eventRepository.getAllByIdIn(eventsIds).stream().map(event -> {
            EventShortDto shortDto = EventMapper.toShortDto(event);
            shortDto.setCategory(CategoryMapper.toDto(categoryRepository.getReferenceById(event.getCategory())));
            shortDto.setInitiator(UserMapper.toShortDto(userRepository.getReferenceById(event.getInitiator())));
            return shortDto;
        }).collect(Collectors.toList());
    }
}
