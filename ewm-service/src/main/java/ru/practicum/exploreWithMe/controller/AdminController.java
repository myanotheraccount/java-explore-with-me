package ru.practicum.exploreWithMe.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.exploreWithMe.dto.CategoryDto;
import ru.practicum.exploreWithMe.dto.CommentStateEnum;
import ru.practicum.exploreWithMe.dto.CompilationDto;
import ru.practicum.exploreWithMe.dto.EventCommentDto;
import ru.practicum.exploreWithMe.dto.EventFullDto;
import ru.practicum.exploreWithMe.dto.NewCompilationDto;
import ru.practicum.exploreWithMe.dto.NewEventDto;
import ru.practicum.exploreWithMe.dto.UserDto;
import ru.practicum.exploreWithMe.service.CategoryService;
import ru.practicum.exploreWithMe.service.CompilationService;
import ru.practicum.exploreWithMe.service.EventService;
import ru.practicum.exploreWithMe.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@Validated
@RestController
@RequestMapping("/admin")
public class AdminController {
    private final CategoryService categoryService;
    private final UserService userService;
    private final EventService eventService;
    private final CompilationService compilationService;

    public AdminController(CategoryService categoryService, UserService userService, EventService eventService, CompilationService compilationService) {
        this.categoryService = categoryService;
        this.userService = userService;
        this.eventService = eventService;
        this.compilationService = compilationService;
    }

    @PostMapping("/categories")
    public CategoryDto addCategory(@RequestBody @Valid CategoryDto categoryDto) {
        return categoryService.saveCategory(categoryDto);
    }

    @PatchMapping("/categories")
    public CategoryDto updateCategory(@RequestBody @Valid CategoryDto categoryDto) {
        return categoryService.updateCategory(categoryDto);
    }

    @DeleteMapping("/categories/{catId}")
    public void deleteCategory(@PathVariable Long catId) {
        categoryService.deleteCategory(catId);
    }

    @GetMapping("/users")
    public List<UserDto> getUsers(@RequestParam(value = "ids") List<Long> ids) {
        return userService.getAll(ids);
    }

    @PostMapping("/users")
    public UserDto addUser(@RequestBody UserDto userDto) {
        return userService.saveUser(userDto);
    }

    @DeleteMapping("/users/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
    }

    @GetMapping("/events")
    public List<EventFullDto> findEvents(
            @RequestParam(value = "users", required = false) List<Long> userIds,
            @RequestParam(value = "states", required = false) List<String> state,
            @RequestParam(value = "categories", required = false) List<Long> categoriesIds,
            @RequestParam(value = "rangeStart", required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(value = "rangeEnd", required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @PositiveOrZero @RequestParam(value = "from", required = false, defaultValue = "0") int from,
            @Positive @RequestParam(value = "size", required = false, defaultValue = "50") int size
    ) {
        return eventService.findEvents(
                userIds,
                state,
                categoriesIds,
                rangeStart,
                rangeEnd,
                PageRequest.of(from / size, size)
        );
    }

    @PatchMapping("/events/{eventId}/publish")
    public EventFullDto publishEvent(@PathVariable Long eventId) {
        return eventService.publishEvent(eventId);
    }

    @PatchMapping("/events/{eventId}/reject")
    public EventFullDto rejectEvent(@PathVariable Long eventId) {
        return eventService.rejectEventByAdmin(eventId);
    }

    @PutMapping("/events/{eventId}")
    public EventFullDto updateEvent(
            @PathVariable Long eventId,
            @RequestBody NewEventDto newEventDto
    ) {
        return eventService.updateEventByAdmin(eventId, newEventDto);
    }

    @GetMapping("/comments")
    public List<EventCommentDto> getEventComments(
            @PositiveOrZero @RequestParam(value = "from", required = false, defaultValue = "0") int from,
            @Positive @RequestParam(value = "size", required = false, defaultValue = "50") int size
    ) {
        return eventService.getEventCommentsByAdmin(CommentStateEnum.NEED_MODERATION, PageRequest.of(from / size, size));
    }

    @PatchMapping("/comments/{commentId}")
    public EventCommentDto moderateEventComment(
            @PathVariable Long commentId,
            @RequestParam Boolean isPublished
    ) {
        return eventService.moderateComment(commentId, isPublished);
    }

    @PostMapping("/compilations")
    public CompilationDto addCompilation(@RequestBody NewCompilationDto newCompilationDto) {
        return compilationService.addCompilation(newCompilationDto);
    }

    @PatchMapping("/compilations/{compId}/events/{eventId}")
    public void addCompilation(
            @PathVariable Long compId,
            @PathVariable Long eventId
    ) {
        compilationService.addCompilationEvent(compId, eventId);
    }

    @PatchMapping("/compilations/{compId}/pin")
    public void pinCompilation(@PathVariable Long compId) {
        compilationService.pinById(compId, true);
    }

    @DeleteMapping("/compilations/{compId}")
    public void deleteCompilation(@PathVariable Long compId) {
        compilationService.deleteCompilation(compId);
    }

    @DeleteMapping("/compilations/{compId}/events/{eventId}")
    public void deleteCompilationEvent(
            @PathVariable Long compId,
            @PathVariable Long eventId
    ) {
        compilationService.deleteCompilationEvent(compId, eventId);
    }

    @DeleteMapping("/compilations/{compId}/pin")
    public void unPinCompilation(@PathVariable Long compId) {
        compilationService.pinById(compId, false);
    }
}
