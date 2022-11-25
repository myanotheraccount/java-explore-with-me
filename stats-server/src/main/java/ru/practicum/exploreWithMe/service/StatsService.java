package ru.practicum.exploreWithMe.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exploreWithMe.dto.EndpointHitDto;
import ru.practicum.exploreWithMe.dto.ViewStatsDto;
import ru.practicum.exploreWithMe.mappers.EndpointMapper;
import ru.practicum.exploreWithMe.repository.StatsRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Slf4j
@Transactional(readOnly = true)
public class StatsService {
    private final StatsRepository statsRepository;
    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public StatsService(StatsRepository statsRepository) {
        this.statsRepository = statsRepository;
    }

    @Transactional
    public void add(EndpointHitDto endpointHitDto) {
        statsRepository.save(EndpointMapper.fromDto(endpointHitDto));
    }

    public List<ViewStatsDto> get(String start, String end, List<String> uris, Boolean unique) {
        LocalDateTime from = LocalDateTime.parse(start, formatter);
        LocalDateTime to = LocalDateTime.parse(end, formatter);
        if (unique) {
            return statsRepository.getUniqueByFilter(from, to, uris);
        }
        return statsRepository.getByFilter(from, to, uris);
    }
}
