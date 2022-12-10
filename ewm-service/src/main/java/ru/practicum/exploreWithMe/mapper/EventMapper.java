package ru.practicum.exploreWithMe.mapper;

import ru.practicum.exploreWithMe.dto.EventCommentDto;
import ru.practicum.exploreWithMe.dto.EventFullDto;
import ru.practicum.exploreWithMe.dto.EventShortDto;
import ru.practicum.exploreWithMe.dto.Location;
import ru.practicum.exploreWithMe.dto.NewEventDto;
import ru.practicum.exploreWithMe.dto.ParticipationRequestDto;
import ru.practicum.exploreWithMe.dto.EventStateEnum;
import ru.practicum.exploreWithMe.model.Event;
import ru.practicum.exploreWithMe.model.EventComment;
import ru.practicum.exploreWithMe.model.EventRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EventMapper {
    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static Event fromNewDto(NewEventDto eventDto, Long userId) {
        Event event = Event.builder()
                .title(eventDto.getTitle())
                .annotation(eventDto.getAnnotation())
                .description(eventDto.getDescription())
                .category(eventDto.getCategory())
                .initiator(userId)
                .created(LocalDateTime.now())
                .eventDate(LocalDateTime.parse(eventDto.getEventDate(), formatter))
                .paid(eventDto.getPaid())
                .participantLimit(eventDto.getParticipantLimit())
                .requestModeration(eventDto.getRequestModeration())
                .state(EventStateEnum.PENDING.toString())
                .build();

        if (eventDto.getLocation() != null) {
            event.setLat(eventDto.getLocation().getLat());
            event.setLon(eventDto.getLocation().getLon());
        }

        return event;
    }

    public static EventFullDto toFullDto(Event event) {
        return EventFullDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .title(event.getTitle())
                .description(event.getDescription())
                .createdOn(event.getEventDate())
                .eventDate(event.getEventDate())
                .location(new Location(event.getLat(), event.getLon()))
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublished() == null ? null : event.getPublished())
                .requestModeration(event.getRequestModeration())
                .state(EventStateEnum.valueOf(event.getState()))
                .build();
    }

    public static ParticipationRequestDto toParticipationDto(EventRequest eventRequest) {
        return new ParticipationRequestDto(
                eventRequest.getId(),
                eventRequest.getCreated().format(formatter),
                eventRequest.getEvent(),
                eventRequest.getRequester(),
                eventRequest.getStatus()
        );
    }

    public static EventShortDto toShortDto(Event event) {
        return EventShortDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .annotation(event.getAnnotation())
                .eventDate(event.getEventDate())
                .paid(event.getPaid())
                .build();
    }

    public static EventComment fromCommentDto(EventCommentDto dto, Long userId, Long eventId) {
        return EventComment.builder()
                .id(dto.getId())
                .userId(userId)
                .eventId(eventId)
                .comment(dto.getComment())
                .build();
    }

    public static EventCommentDto toCommentDto(EventComment comment) {
        return new EventCommentDto(
                comment.getId(),
                comment.getComment(),
                comment.getDateTime()
        );
    }
}