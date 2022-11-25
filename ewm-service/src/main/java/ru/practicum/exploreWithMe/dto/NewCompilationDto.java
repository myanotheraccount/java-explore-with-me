package ru.practicum.exploreWithMe.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@AllArgsConstructor
@ToString
public class NewCompilationDto {
    private List<Long> events;
    private Boolean pinned;
    private String title;
}
