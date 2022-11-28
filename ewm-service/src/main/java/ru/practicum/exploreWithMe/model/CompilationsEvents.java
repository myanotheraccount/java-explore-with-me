package ru.practicum.exploreWithMe.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "compilations_events", schema = "public")
@Getter
@Setter
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
