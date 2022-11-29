package ru.practicum.exploreWithMe.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.exploreWithMe.model.EventComment;

public interface EventCommentRepository extends JpaRepository<EventComment, Long> {
    Page<EventComment> getAllByEventIdAndPublishedTrue(Long eventId, Pageable pageable);
}
