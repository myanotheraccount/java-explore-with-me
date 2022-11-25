package ru.practicum.exploreWithMe.mappers;

import ru.practicum.exploreWithMe.dto.EndpointHitDto;
import ru.practicum.exploreWithMe.model.EndpointHit;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EndpointMapper {
    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static EndpointHit fromDto(EndpointHitDto dto) {
        return new EndpointHit(
                dto.getId(),
                dto.getApp(),
                dto.getIp(),
                LocalDateTime.parse(dto.getTimestamp(), formatter),
                dto.getUri()
        );
    }
}
