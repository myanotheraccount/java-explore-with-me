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
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional
    public CategoryDto saveCategory(CategoryDto categoryDto) {
        checkCategory(categoryDto);
        log.info(String.format("сохранение категории: %s", categoryDto.getName()));
        return CategoryMapper.toDto(categoryRepository.save(CategoryMapper.fromDto(categoryDto)));
    }

    @Override
    public CategoryDto getCategory(Long id) {
        try {
            Category category = categoryRepository.getReferenceById(id);
            log.info(String.format("найдена категория с id = %d", id));
            return CategoryMapper.toDto(category);
        } catch (Exception e) {
            throw new NotFoundException(String.format("категория с id = %d не найдена", id));
        }
    }

    @Override
    public List<CategoryDto> getAll(Pageable pageable) {
        log.info("получение списка категорий");
        return categoryRepository.findAll(pageable).stream().map(CategoryMapper::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(CategoryDto categoryDto) {
        checkCategory(categoryDto);
        log.info(String.format("обновление категории %d", categoryDto.getId()));
        return CategoryMapper.toDto(categoryRepository.save(CategoryMapper.fromDto(categoryDto)));
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        log.info(String.format("удаление категории %d", id));
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
