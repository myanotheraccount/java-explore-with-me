package ru.practicum.exploreWithMe.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "events_requests", schema = "public")
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created")
    private LocalDateTime created;

    @Column(name = "event")
    private Long event;

    @Column(name = "requester")
    private Long requester;

    @Column(name = "status")
    private String status;
}
