package ru.practicum.exploreWithMe.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class EventFullDto   {
  private Long id;
  private String annotation;
  private CategoryDto category;
  private Long confirmedRequests;
  private String createdOn;
  private String description;
  private String eventDate;
  private UserShortDto initiator;
  private Location location;
  private Boolean paid;
  private Integer participantLimit;
  private String publishedOn;
  private Boolean requestModeration;
  private EventStateEnum state;
  private String title;
  private Long views;
}
