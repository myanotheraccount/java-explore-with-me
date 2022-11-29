package ru.practicum.exploreWithMe.mapper;

import ru.practicum.exploreWithMe.dto.CategoryDto;
import ru.practicum.exploreWithMe.model.Category;

public class CategoryMapper {
    public static CategoryDto toDto(Category category) {
        return new CategoryDto(category.getId(), category.getName());
    }

    public static Category fromDto(CategoryDto categoryDto) {
        return new Category(categoryDto.getId(), categoryDto.getName());
    }
}
