package ru.practicum.exploreWithMe.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exploreWithMe.dto.CategoryDto;
import ru.practicum.exploreWithMe.exceptions.ConflictException;
import ru.practicum.exploreWithMe.exceptions.NotFoundException;
import ru.practicum.exploreWithMe.exceptions.ValidationException;
import ru.practicum.exploreWithMe.mappers.CategoryMapper;
import ru.practicum.exploreWithMe.models.Category;
import ru.practicum.exploreWithMe.repository.CategoryRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public CategoryDto saveCategory(CategoryDto categoryDto) {
        checkCategory(categoryDto);
        return CategoryMapper.toDto(categoryRepository.save(CategoryMapper.fromDto(categoryDto)));
    }

    public CategoryDto getCategory(Long id) {
        try {
            Category category = categoryRepository.getReferenceById(id);
            log.info(String.format("найдена категория с id = %d", id));
            return CategoryMapper.toDto(category);
        } catch (Exception e) {
            throw new NotFoundException(String.format("категория с id = %d не найдена", id));
        }
    }

    public List<CategoryDto> getAll(Pageable pageable) {
        return categoryRepository.findAll(pageable).stream().map(CategoryMapper::toDto).collect(Collectors.toList());
    }

    @Transactional
    public CategoryDto updateCategory(CategoryDto categoryDto) {
        checkCategory(categoryDto);
        return CategoryMapper.toDto(categoryRepository.save(CategoryMapper.fromDto(categoryDto)));
    }

    @Transactional
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }

    private void checkCategory(CategoryDto categoryDto) {
        if (categoryDto.getName() == null) {
            throw new ValidationException("поле имя категории не может быть пустым");
        }
        if (categoryRepository.existsByName(categoryDto.getName())) {
            throw new ConflictException("имя категории не уникально");
        }
    }

}
