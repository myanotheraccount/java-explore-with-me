package ru.practicum.exploreWithMe.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.exploreWithMe.dto.EventFullDto;
import ru.practicum.exploreWithMe.dto.EventShortDto;
import ru.practicum.exploreWithMe.dto.NewEventDto;
import ru.practicum.exploreWithMe.dto.ParticipationRequestDto;
import ru.practicum.exploreWithMe.dto.ParticipationStateEnum;
import ru.practicum.exploreWithMe.dto.UpdateEventRequest;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    EventFullDto getEvent(Long eventId);

    EventFullDto addEvent(NewEventDto newEvent, Long userId);

    ParticipationRequestDto addRequest(Long userId, Long eventId);

    ParticipationRequestDto cancelRequest(Long userId, Long requestId);

    List<ParticipationRequestDto> getUserRequests(Long userId);

    List<ParticipationRequestDto> getUserEventRequests(Long userId, Long eventId);

    List<EventFullDto> findEvents(List<Long> userIds, List<String> states, List<Long> categories, LocalDateTime start, LocalDateTime end, Pageable pageable);

    EventFullDto publishEvent(Long eventId);

    EventFullDto rejectEventByAdmin(Long eventId);

    EventFullDto updateEventByAdmin(Long eventId, NewEventDto newEventDto);

    EventFullDto rejectEvent(Long userId, Long eventId);

    List<EventShortDto> getUserEvents(Long userId, Pageable pageable);

    EventFullDto getUserEvent(Long userId, Long eventId);

    EventFullDto updateUserEvent(Long userId, UpdateEventRequest eventRequest);

    ParticipationRequestDto updateUserEventRequest(Long userId, Long eventId, Long requestId, ParticipationStateEnum stateEnum);
}
