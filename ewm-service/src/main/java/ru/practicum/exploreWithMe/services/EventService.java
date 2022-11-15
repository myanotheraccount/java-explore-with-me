package ru.practicum.exploreWithMe.services;

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
import ru.practicum.exploreWithMe.exceptions.ValidationException;
import ru.practicum.exploreWithMe.mappers.CategoryMapper;
import ru.practicum.exploreWithMe.mappers.EventMapper;
import ru.practicum.exploreWithMe.mappers.UserMapper;
import ru.practicum.exploreWithMe.models.Event;
import ru.practicum.exploreWithMe.models.EventRequest;
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
public class EventService {
    private final EventRepository eventRepository;
    private final EventsRequestsRepository eventsRequestsRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public EventService(EventRepository eventRepository, EventsRequestsRepository eventsRequestsRepository, CategoryRepository categoryRepository, UserRepository userRepository) {
        this.eventRepository = eventRepository;
        this.eventsRequestsRepository = eventsRequestsRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    public EventFullDto getEvent(Long eventId) {
        return toFullDto(eventRepository.getReferenceById(eventId));
    }

    @Transactional
    public EventFullDto addEvent(NewEventDto newEvent, Long userId) {
        try {
            return toFullDto(eventRepository.save(EventMapper.fromNewDto(newEvent, userId)));
        } catch (Exception e) {
            throw new ValidationException("не удалось сохранить событие");
        }
    }

    @Transactional
    public ParticipationRequestDto addRequest(Long userId, Long eventId) {
        return EventMapper.toParticipationDto(eventsRequestsRepository.save(EventRequest.builder()
                .requester(userId)
                .event(eventId)
                .created(LocalDateTime.now())
                .status(ParticipationStateEnum.PENDING.toString())
                .build()));
    }

    @Transactional
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        EventRequest eventRequest = eventsRequestsRepository.getReferenceById(requestId);
        if (eventRequest.getRequester().equals(userId)) {
            eventRequest.setStatus(EventStateEnum.CANCELED.toString());
            return EventMapper.toParticipationDto(eventsRequestsRepository.save(eventRequest));
        }
        throw new ValidationException("Только автор заявки может отменить участие");
    }

    public List<ParticipationRequestDto> getUserRequests(Long userId) {
        return eventsRequestsRepository.findAllByRequester(userId)
                .stream().map(EventMapper::toParticipationDto).collect(Collectors.toList());
    }

    public List<ParticipationRequestDto> getUserEventRequests(Long userId, Long eventId) {
        List<Long> eventsIds = eventRepository.getAllByInitiator(userId).stream().map(Event::getId).collect(Collectors.toList());
        if (eventsIds.contains(eventId)) {
            return eventsRequestsRepository.findAllByEvent(eventId)
                    .stream().map(EventMapper::toParticipationDto).collect(Collectors.toList());
        }
        throw new ValidationException("Только автор события может смотреть список заявок");
    }

    public List<EventFullDto> findEvents(List<Long> userIds, List<String> states, List<Long> categories, LocalDateTime start, LocalDateTime end, Pageable pageable) {
        return eventRepository.findEvents(userIds, states, categories, start, end, pageable).stream().map(this::toFullDto).collect(Collectors.toList());
    }

    @Transactional
    public EventFullDto publishEvent(Long eventId) {
        Event event = eventRepository.getReferenceById(eventId);
        event.setPublished(LocalDateTime.now());
        event.setState(EventStateEnum.PUBLISHED.toString());
        return toFullDto(eventRepository.save(event));
    }

    @Transactional
    public EventFullDto rejectEventByAdmin(Long eventId) {
        Event event = eventRepository.getReferenceById(eventId);
        event.setState(EventStateEnum.CANCELED.toString());
        return toFullDto(eventRepository.save(event));
    }

    @Transactional
    public EventFullDto updateEventByAdmin(Long eventId, NewEventDto newEventDto) {
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

    @Transactional
    public EventFullDto rejectEvent(Long userId, Long eventId) {
        Event event = eventRepository.getReferenceById(eventId);
        if (event.getInitiator().equals(userId)) {
            event.setState(EventStateEnum.CANCELED.toString());
            return toFullDto(eventRepository.save(event));
        }
        throw new ValidationException("Только автор события может отменить событие");
    }

    public List<EventShortDto> getUserEvents(Long userId, Pageable pageable) {
        return eventRepository.findAllByInitiator(userId, pageable).stream().map(this::toShortDto).collect(Collectors.toList());
    }

    public EventFullDto getUserEvent(Long userId, Long eventId) {
        return toFullDto(eventRepository.findByInitiatorAndId(userId, eventId));
    }

    @Transactional
    public EventFullDto updateUserEvent(Long userId, UpdateEventRequest eventRequest) {
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

    @Transactional
    public ParticipationRequestDto updateUserEventRequest(Long userId, Long eventId, Long requestId, ParticipationStateEnum stateEnum) {
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
