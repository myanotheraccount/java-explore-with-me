package ru.practicum.exploreWithMe.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class EndpointHitDto {
    private Long id;
    private String app;
    private String ip;
    private String timestamp;
    private String uri;
}
