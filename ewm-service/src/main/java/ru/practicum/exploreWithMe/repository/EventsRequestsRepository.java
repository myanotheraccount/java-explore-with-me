package ru.practicum.exploreWithMe.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.exploreWithMe.models.EventRequest;

import java.util.List;

public interface EventsRequestsRepository extends JpaRepository<EventRequest, Long> {
    List<EventRequest> findAllByRequester(Long requester);

    List<EventRequest> findAllByEvent(Long event);
}
