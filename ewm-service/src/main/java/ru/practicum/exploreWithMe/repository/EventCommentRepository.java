package ru.practicum.exploreWithMe.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.exploreWithMe.model.EventComment;

public interface EventCommentRepository extends JpaRepository<EventComment, Long> {
    Page<EventComment> getAllByEventIdAndState(Long eventId, String state, Pageable pageable);

    Page<EventComment> getAllByState(String state, Pageable pageable);

    Page<EventComment> getAllByUserId(Long userId, Pageable pageable);
}
