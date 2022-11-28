package ru.practicum.exploreWithMe.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.exploreWithMe.model.Event;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    @Query(value = "SELECT e FROM Event e " +
            "WHERE (e.initiator IN ?1 OR ?1 IS NULL) " +
            "AND (e.state IN ?2 or ?2 IS NULL) " +
            "AND (e.category IN ?3 or ?3 IS NULL) " +
            "AND e.eventDate BETWEEN ?4 AND ?5")
    Page<Event> findEvents(List<Long> userIds, List<String> state, List<Long> categoriesIds, LocalDateTime rangeStart, LocalDateTime rangeEnd, Pageable pageable);

    Page<Event> findAllByInitiator(Long userId, Pageable pageable);

    List<Event> getAllByInitiator(Long userId);

    List<Event> getAllByIdIn(List<Long> id);

    Event findByInitiatorAndId(Long userId, Long id);
}
