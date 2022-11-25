package ru.practicum.exploreWithMe.controllers;

import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.exploreWithMe.client.StatsServerClient;
import ru.practicum.exploreWithMe.dto.EventFullDto;
import ru.practicum.exploreWithMe.services.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@Validated
@RestController
@RequestMapping("/events")
public class EventController {
    private final EventService eventService;
    private final StatsServerClient statsServerClient;

    public EventController(EventService eventService, StatsServerClient statsServerClient) {
        this.eventService = eventService;
        this.statsServerClient = statsServerClient;
    }

    @GetMapping
    public List<EventFullDto> findEvents(
            @RequestParam(value = "users", required = false) List<Long> userIds,
            @RequestParam(value = "states", required = false) List<String> state,
            @RequestParam(value = "categories", required = false) List<Long> categoriesIds,
            @RequestParam(value = "rangeStart", required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(value = "rangeEnd", required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @PositiveOrZero @RequestParam(value = "from", required = false, defaultValue = "0") int from,
            @Positive @RequestParam(value = "size", required = false, defaultValue = "50") int size,
            HttpServletRequest request
    ) {
        statsServerClient.setStat(request);

        return eventService.findEvents(
                userIds,
                state,
                categoriesIds,
                rangeStart,
                rangeEnd,
                PageRequest.of(from / size, size)
        );
    }


    @GetMapping("/{id}")
    public EventFullDto getUserEvent(@PathVariable Long id, HttpServletRequest request) {
        statsServerClient.setStat(request);
        return eventService.getEvent(id);
    }
}
