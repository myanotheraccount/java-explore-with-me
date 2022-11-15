package ru.practicum.exploreWithMe.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.exploreWithMe.models.CompilationsEvents;

import java.util.List;

public interface CompilationsEventsRepository extends JpaRepository<CompilationsEvents, Long> {
    List<CompilationsEvents> getAllByCompilation(Long compid);
}
