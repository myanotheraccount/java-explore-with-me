package ru.practicum.exploreWithMe.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.exploreWithMe.dto.EndpointHitDto;
import ru.practicum.exploreWithMe.dto.ViewStatsDto;
import ru.practicum.exploreWithMe.service.StatsService;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@Validated
public class StatsController {
    private final StatsService statsService;

    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    @PostMapping("/hit")
    public void createHitStatController(@RequestBody EndpointHitDto endpointHitDto) {
        statsService.add(endpointHitDto);
    }

    @GetMapping("/stats")
    public List<ViewStatsDto> getStatController(
            @NotNull
            @RequestParam(value = "start") String start,
            @NotNull
            @RequestParam(value = "end") String end,
            @RequestParam(value = "uris", required = false) List<String> uris,
            @RequestParam(value = "unique", required = false, defaultValue = "false") Boolean unique) {
        return statsService.get(start, end, uris, unique);
    }
}
