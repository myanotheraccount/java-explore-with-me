package ru.practicum.exploreWithMe.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@AllArgsConstructor
@ToString
public class UserDto {
    private Long id;
    private String email;
    private String name;
}
