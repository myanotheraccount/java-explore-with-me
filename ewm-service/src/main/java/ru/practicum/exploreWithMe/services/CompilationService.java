package ru.practicum.exploreWithMe.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exploreWithMe.dto.CompilationDto;
import ru.practicum.exploreWithMe.dto.EventShortDto;
import ru.practicum.exploreWithMe.dto.NewCompilationDto;
import ru.practicum.exploreWithMe.exceptions.ValidationException;
import ru.practicum.exploreWithMe.mappers.CategoryMapper;
import ru.practicum.exploreWithMe.mappers.CompilationMapper;
import ru.practicum.exploreWithMe.mappers.EventMapper;
import ru.practicum.exploreWithMe.mappers.UserMapper;
import ru.practicum.exploreWithMe.models.Compilation;
import ru.practicum.exploreWithMe.models.CompilationsEvents;
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
public class CompilationService {
    private final CompilationRepository compilationRepository;
    private final CompilationsEventsRepository compilationsEventsRepository;
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public CompilationService(CompilationRepository compilationRepository, CompilationsEventsRepository compilationsEventsRepository, EventRepository eventRepository, CategoryRepository categoryRepository, UserRepository userRepository) {
        this.compilationRepository = compilationRepository;
        this.compilationsEventsRepository = compilationsEventsRepository;
        this.eventRepository = eventRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    public CompilationDto getById(Long id) {
        CompilationDto dto = CompilationMapper.toDto(compilationRepository.getReferenceById(id));
        List<Long> eventIds = compilationsEventsRepository.getAllByCompilation(id).stream()
                .map(CompilationsEvents::getEvent).collect(Collectors.toList());
        dto.setEvents(toEventShortDto(eventIds));
        return dto;
    }

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
        return dto;
    }

    public List<CompilationDto> getPinned(boolean pinned, Pageable pageable) {
        Page<Compilation> compilations = compilationRepository.findAllByPinned(pinned, pageable);
        return compilations.stream().map(compilation -> {
            CompilationDto dto = CompilationMapper.toDto(compilation);
            List<Long> eventIds = compilationsEventsRepository.getAllByCompilation(compilation.getId()).stream()
                    .map(CompilationsEvents::getEvent).collect(Collectors.toList());
            dto.setEvents(toEventShortDto(eventIds));
            return dto;
        }).collect(Collectors.toList());
    }

    @Transactional
    public void addCompilationEvent(Long compId, Long eventId) {
        compilationsEventsRepository.save(new CompilationsEvents(compId, eventId));
    }

    @Transactional
    public void pinById(Long compId, Boolean pinned) {
        Compilation compilation = compilationRepository.getReferenceById(compId);
        compilation.setPinned(pinned);
        compilationRepository.save(compilation);
    }

    @Transactional
    public void deleteCompilation(Long compId) {
        compilationRepository.deleteById(compId);
    }

    @Transactional
    public void deleteCompilationEvent(Long compId, Long eventId) {
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
