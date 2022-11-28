package ru.practicum.exploreWithMe.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exploreWithMe.dto.EventFullDto;
import ru.practicum.exploreWithMe.dto.EventShortDto;
import ru.practicum.exploreWithMe.dto.NewEventDto;
import ru.practicum.exploreWithMe.dto.ParticipationRequestDto;
import ru.practicum.exploreWithMe.dto.EventStateEnum;
import ru.practicum.exploreWithMe.dto.ParticipationStateEnum;
import ru.practicum.exploreWithMe.dto.UpdateEventRequest;
import ru.practicum.exploreWithMe.exception.ValidationException;
import ru.practicum.exploreWithMe.mapper.CategoryMapper;
import ru.practicum.exploreWithMe.mapper.EventMapper;
import ru.practicum.exploreWithMe.mapper.UserMapper;
import ru.practicum.exploreWithMe.model.Event;
import ru.practicum.exploreWithMe.model.EventRequest;
import ru.practicum.exploreWithMe.repository.CategoryRepository;
import ru.practicum.exploreWithMe.repository.EventRepository;
import ru.practicum.exploreWithMe.repository.EventsRequestsRepository;
import ru.practicum.exploreWithMe.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final EventsRequestsRepository eventsRequestsRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public EventServiceImpl(EventRepository eventRepository, EventsRequestsRepository eventsRequestsRepository, CategoryRepository categoryRepository, UserRepository userRepository) {
        this.eventRepository = eventRepository;
        this.eventsRequestsRepository = eventsRequestsRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    @Override
    public EventFullDto getEvent(Long eventId) {
        log.info(String.format("получения события %d", eventId));
        return toFullDto(eventRepository.getReferenceById(eventId));
    }

    @Override
    @Transactional
    public EventFullDto addEvent(NewEventDto newEvent, Long userId) {
        try {
            log.info(String.format("добавление события %s", newEvent));
            return toFullDto(eventRepository.save(EventMapper.fromNewDto(newEvent, userId)));
        } catch (Exception e) {
            throw new ValidationException("не удалось сохранить событие");
        }
    }

    @Override
    @Transactional
    public ParticipationRequestDto addRequest(Long userId, Long eventId) {
        log.info(String.format("добавление заявки от %d на участие в событии %d", userId, eventId));
        return EventMapper.toParticipationDto(eventsRequestsRepository.save(EventRequest.builder()
                .requester(userId)
                .event(eventId)
                .created(LocalDateTime.now())
                .status(ParticipationStateEnum.PENDING.toString())
                .build()));
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        log.info(String.format("отмента заявки %d на участие от %d", requestId, userId));
        EventRequest eventRequest = eventsRequestsRepository.getReferenceById(requestId);
        if (eventRequest.getRequester().equals(userId)) {
            eventRequest.setStatus(EventStateEnum.CANCELED.toString());
            return EventMapper.toParticipationDto(eventsRequestsRepository.save(eventRequest));
        }
        throw new ValidationException("Только автор заявки может отменить участие");
    }

    @Override
    public List<ParticipationRequestDto> getUserRequests(Long userId) {
        log.info(String.format("получение всех заявок пользователя %d", userId));
        return eventsRequestsRepository.findAllByRequester(userId)
                .stream().map(EventMapper::toParticipationDto).collect(Collectors.toList());
    }

    @Override
    public List<ParticipationRequestDto> getUserEventRequests(Long userId, Long eventId) {
        log.info(String.format("получения списка заявок пользователем %d события %d", userId, eventId));
        List<Long> eventsIds = eventRepository.getAllByInitiator(userId).stream().map(Event::getId).collect(Collectors.toList());
        if (eventsIds.contains(eventId)) {
            return eventsRequestsRepository.findAllByEvent(eventId)
                    .stream().map(EventMapper::toParticipationDto).collect(Collectors.toList());
        }
        throw new ValidationException("Только автор события может смотреть список заявок");
    }

    @Override
    public List<EventFullDto> findEvents(List<Long> userIds, List<String> states, List<Long> categories, LocalDateTime start, LocalDateTime end, Pageable pageable) {
        log.info("поиск заявок");
        return eventRepository.findEvents(userIds, states, categories, start, end, pageable).stream().map(this::toFullDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventFullDto publishEvent(Long eventId) {
        log.info(String.format("публикация события %d", eventId));
        Event event = eventRepository.getReferenceById(eventId);
        event.setPublished(LocalDateTime.now());
        event.setState(EventStateEnum.PUBLISHED.toString());
        return toFullDto(eventRepository.save(event));
    }

    @Override
    @Transactional
    public EventFullDto rejectEventByAdmin(Long eventId) {
        log.info(String.format("отмена события %d админом", eventId));
        Event event = eventRepository.getReferenceById(eventId);
        event.setState(EventStateEnum.CANCELED.toString());
        return toFullDto(eventRepository.save(event));
    }

    @Override
    @Transactional
    public EventFullDto updateEventByAdmin(Long eventId, NewEventDto newEventDto) {
        log.info(String.format("обновление события %d", eventId));
        Event event = eventRepository.getReferenceById(eventId);

        if (newEventDto.getAnnotation() != null) {
            event.setAnnotation(newEventDto.getAnnotation());
        }
        if (newEventDto.getCategory() != null) {
            event.setCategory(newEventDto.getCategory());
        }
        if (newEventDto.getDescription() != null) {
            event.setDescription(newEventDto.getDescription());
        }
        if (newEventDto.getEventDate() != null) {
            event.setEventDate(LocalDateTime.parse(newEventDto.getEventDate(), EventMapper.formatter));
        }
        if (newEventDto.getPaid() != null) {
            event.setPaid(newEventDto.getPaid());
        }
        if (newEventDto.getLocation() != null) {
            event.setLat(newEventDto.getLocation().getLat());
            event.setLon(newEventDto.getLocation().getLon());
        }
        if (newEventDto.getParticipantLimit() != null) {
            event.setParticipantLimit(newEventDto.getParticipantLimit());
        }
        if (newEventDto.getRequestModeration() != null) {
            event.setRequestModeration(newEventDto.getRequestModeration());
        }
        if (newEventDto.getTitle() != null) {
            event.setTitle(newEventDto.getTitle());
        }
        return toFullDto(eventRepository.save(event));
    }

    @Override
    @Transactional
    public EventFullDto rejectEvent(Long userId, Long eventId) {
        log.info(String.format("отмена события %d пользователем %d", eventId, userId));
        Event event = eventRepository.getReferenceById(eventId);
        if (event.getInitiator().equals(userId)) {
            event.setState(EventStateEnum.CANCELED.toString());
            return toFullDto(eventRepository.save(event));
        }
        throw new ValidationException("Только автор события может отменить событие");
    }

    @Override
    public List<EventShortDto> getUserEvents(Long userId, Pageable pageable) {
        log.info(String.format("получения списка событий пользователя %d", userId));
        return eventRepository.findAllByInitiator(userId, pageable).stream().map(this::toShortDto).collect(Collectors.toList());
    }

    @Override
    public EventFullDto getUserEvent(Long userId, Long eventId) {
        log.info(String.format("получения события %d пользователем %d", eventId, userId));
        return toFullDto(eventRepository.findByInitiatorAndId(userId, eventId));
    }

    @Override
    @Transactional
    public EventFullDto updateUserEvent(Long userId, UpdateEventRequest eventRequest) {
        log.info(String.format("обновление события %d пользователем %d", eventRequest.getEventId(), userId));
        Event event = eventRepository.getReferenceById(eventRequest.getEventId());
        if (!event.getInitiator().equals(userId)) {
            throw new ValidationException("Только автор события может изменять событие");
        }
        if (eventRequest.getAnnotation() != null) {
            event.setAnnotation(eventRequest.getAnnotation());
        }
        if (eventRequest.getCategory() != null) {
            event.setCategory(eventRequest.getCategory());
        }
        if (eventRequest.getDescription() != null) {
            event.setDescription(eventRequest.getDescription());
        }
        if (eventRequest.getEventDate() != null) {
            event.setEventDate(LocalDateTime.parse(eventRequest.getEventDate(), EventMapper.formatter));
        }
        if (eventRequest.getPaid() != null) {
            event.setPaid(eventRequest.getPaid());
        }
        if (eventRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(eventRequest.getParticipantLimit());
        }
        if (eventRequest.getTitle() != null) {
            event.setTitle(eventRequest.getTitle());
        }
        return toFullDto(eventRepository.save(event));
    }

    @Override
    @Transactional
    public ParticipationRequestDto updateUserEventRequest(Long userId, Long eventId, Long requestId, ParticipationStateEnum stateEnum) {
        log.info(String.format("обновление состяния заявки %d события %d пользователем %d", requestId, eventId, userId));
        Event event = eventRepository.getReferenceById(eventId);
        if (event.getInitiator().equals(userId)) {
            EventRequest eventRequest = eventsRequestsRepository.getReferenceById(requestId);
            eventRequest.setStatus(stateEnum.toString());
            return EventMapper.toParticipationDto(eventsRequestsRepository.save(eventRequest));
        }
        throw new ValidationException("Только автор события может изменить заявку на участие");
    }

    private EventFullDto toFullDto(Event event) {
        EventFullDto fullDto = EventMapper.toFullDto(event);
        fullDto.setCategory(CategoryMapper.toDto(categoryRepository.getReferenceById(event.getCategory())));
        fullDto.setInitiator(UserMapper.toShortDto(userRepository.getReferenceById(event.getInitiator())));
        return fullDto;
    }

    private EventShortDto toShortDto(Event event) {
        EventShortDto shortDto = EventMapper.toShortDto(event);
        shortDto.setCategory(CategoryMapper.toDto(categoryRepository.getReferenceById(event.getCategory())));
        shortDto.setInitiator(UserMapper.toShortDto(userRepository.getReferenceById(event.getInitiator())));
        return shortDto;
    }

}
