package ru.practicum.exploreWithMe.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.exploreWithMe.dto.CompilationDto;
import ru.practicum.exploreWithMe.dto.NewCompilationDto;

import java.util.List;

public interface CompilationService {
    CompilationDto getById(Long id);

    CompilationDto addCompilation(NewCompilationDto newCompilationDto);

    List<CompilationDto> getPinned(boolean pinned, Pageable pageable);

    void addCompilationEvent(Long compId, Long eventId);

    void pinById(Long compId, Boolean pinned);

    void deleteCompilation(Long compId);

    void deleteCompilationEvent(Long compId, Long eventId);
}
