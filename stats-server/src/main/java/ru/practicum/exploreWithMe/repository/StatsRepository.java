package ru.practicum.exploreWithMe.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.exploreWithMe.dto.ViewStatsDto;
import ru.practicum.exploreWithMe.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<EndpointHit, Long> {
    @Query(value = "SELECT new ru.practicum.exploreWithMe.dto.ViewStatsDto(e.app,e.uri,COUNT(e.ip)) FROM EndpointHit e WHERE (e.timestamp BETWEEN ?1 AND ?2) AND (e.uri IN ?3 or ?3 IS NULL) GROUP BY e.app,e.uri")
    List<ViewStatsDto> getByFilter(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query(value = "SELECT new ru.practicum.exploreWithMe.dto.ViewStatsDto(e.app,e.uri,COUNT(DISTINCT e.ip)) FROM EndpointHit e WHERE (e.timestamp BETWEEN ?1 AND ?2) AND (e.uri IN ?3 or ?3 IS NULL) GROUP BY e.app,e.uri")
    List<ViewStatsDto> getUniqueByFilter(LocalDateTime start, LocalDateTime end, List<String> uris);
}
