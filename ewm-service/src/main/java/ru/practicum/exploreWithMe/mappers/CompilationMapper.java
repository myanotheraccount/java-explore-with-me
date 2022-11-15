package ru.practicum.exploreWithMe.mappers;

import ru.practicum.exploreWithMe.dto.CompilationDto;
import ru.practicum.exploreWithMe.dto.NewCompilationDto;
import ru.practicum.exploreWithMe.models.Compilation;
import ru.practicum.exploreWithMe.models.CompilationsEvents;

import java.util.List;
import java.util.stream.Collectors;

public class CompilationMapper {
    public static Compilation fromNewDto(NewCompilationDto dto) {
        return Compilation.builder()
                .pinned(dto.getPinned())
                .title(dto.getTitle())
                .build();
    }

    public static List<CompilationsEvents> toCompilationEvents(Long compilationId, List<Long> eventsIds) {
        return eventsIds.stream().map(eventId -> new CompilationsEvents(compilationId, eventId)).collect(Collectors.toList());
    }

    public static CompilationDto toDto(Compilation compilation) {
        return new CompilationDto(
                null,
                compilation.getId(),
                compilation.getPinned(),
                compilation.getTitle()
        );
    }
}
