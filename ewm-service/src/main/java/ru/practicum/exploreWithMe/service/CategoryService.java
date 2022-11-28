package ru.practicum.exploreWithMe.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.exploreWithMe.dto.CategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto saveCategory(CategoryDto categoryDto);

    CategoryDto getCategory(Long id);

    List<CategoryDto> getAll(Pageable pageable);

    CategoryDto updateCategory(CategoryDto categoryDto);

    void deleteCategory(Long id);
}
