package ru.practicum.exploreWithMe.controllers;

import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.exploreWithMe.dto.CompilationDto;
import ru.practicum.exploreWithMe.services.CompilationService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Validated
@RestController
@RequestMapping("/compilations")
public class CompilationsController {

    private final CompilationService compilationService;

    public CompilationsController(CompilationService compilationService) {
        this.compilationService = compilationService;
    }

    @GetMapping("/{compId}")
    public CompilationDto getCompilation(@PathVariable Long compId) {
        return compilationService.getById(compId);
    }

    @GetMapping
    public List<CompilationDto> getPinnedCompilations(
            @RequestParam(required = false) boolean pinned,
            @PositiveOrZero @RequestParam(required = false, defaultValue = "0") int from,
            @Positive @RequestParam(required = false, defaultValue = "50") int size
    ) {
        return compilationService.getPinned(pinned, PageRequest.of(from / size, size));
    }
}
