package ru.practicum.exploreWithMe.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "compilations_events", schema = "public")
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@IdClass(CompilationsEvents.class)
public class CompilationsEvents implements Serializable {
    @Id
    @Column(name = "compilation")
    private Long compilation;

    @Id
    @Column(name = "event")
    private Long event;
}
