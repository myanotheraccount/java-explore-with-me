package ru.practicum.exploreWithMe.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.exploreWithMe.dto.EventCommentDto;
import ru.practicum.exploreWithMe.dto.EventFullDto;
import ru.practicum.exploreWithMe.dto.EventShortDto;
import ru.practicum.exploreWithMe.dto.NewEventDto;
import ru.practicum.exploreWithMe.dto.ParticipationRequestDto;
import ru.practicum.exploreWithMe.dto.ParticipationStateEnum;
import ru.practicum.exploreWithMe.dto.UpdateEventRequest;
import ru.practicum.exploreWithMe.service.EventService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Validated
@RestController
@RequestMapping("/users")
public class UserController {
    private final EventService eventService;

    public UserController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping("/{userId}/events")
    public EventFullDto addEvent(
            @PathVariable Long userId,
            @RequestBody @Valid NewEventDto eventFullDto
    ) {
        return eventService.addEvent(eventFullDto, userId);
    }

    @PostMapping("/{userId}/requests")
    public ParticipationRequestDto addRequest(
            @PathVariable Long userId,
            @RequestParam Long eventId
    ) {
        return eventService.addRequest(userId, eventId);
    }

    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    public ParticipationRequestDto cancelRequest(
            @PathVariable Long userId,
            @PathVariable Long requestId
    ) {
        return eventService.cancelRequest(userId, requestId);
    }

    @GetMapping("/{userId}/requests")
    public List<ParticipationRequestDto> getUserRequests(@PathVariable Long userId) {
        return eventService.getUserRequests(userId);
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    public List<ParticipationRequestDto> getUserEventRequests(
            @PathVariable Long userId,
            @PathVariable Long eventId
    ) {
        return eventService.getUserEventRequests(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}/requests/{requestId}/reject")
    public ParticipationRequestDto rejectUserEventRequest(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @PathVariable Long requestId
    ) {
        return eventService.updateUserEventRequest(userId, eventId, requestId, ParticipationStateEnum.REJECTED);
    }

    @PatchMapping("/{userId}/events/{eventId}/requests/{requestId}/confirm")
    public ParticipationRequestDto confirmUserEventRequest(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @PathVariable Long requestId
    ) {
        return eventService.updateUserEventRequest(userId, eventId, requestId, ParticipationStateEnum.CONFIRMED);
    }

    @GetMapping("/{userId}/events")
    public List<EventShortDto> getUserEvents(
            @PathVariable Long userId,
            @PositiveOrZero @RequestParam(value = "from", required = false, defaultValue = "0") int from,
            @Positive @RequestParam(value = "size", required = false, defaultValue = "50") int size
    ) {
        return eventService.getUserEvents(userId, PageRequest.of(from / size, size));
    }

    @PatchMapping("/{userId}/events")
    public EventFullDto updateUserEvent(
            @PathVariable Long userId,
            @RequestBody UpdateEventRequest updateEventRequest
    ) {
        return eventService.updateUserEvent(userId, updateEventRequest);
    }

    @GetMapping("/{userId}/events/{eventId}")
    public EventFullDto getUserEvent(
            @PathVariable Long userId,
            @PathVariable Long eventId
    ) {
        return eventService.getUserEvent(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}")
    public EventFullDto rejectEvent(
            @PathVariable Long userId,
            @PathVariable Long eventId
    ) {
        return eventService.rejectEvent(userId, eventId);
    }

    @PostMapping("/{userId}/events/{eventId}/comment")
    public EventCommentDto addEventComment(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @RequestBody EventCommentDto commentDto) {
        return eventService.addEventComment(commentDto, userId, eventId);
    }

    @PatchMapping("/{userId}/comments/{commentId}")
    public EventCommentDto updateEventComment(
            @PathVariable Long userId,
            @PathVariable Long commentId,
            @RequestBody EventCommentDto commentDto) {
        return eventService.updateEventComment(commentDto, userId, commentId);
    }
}
