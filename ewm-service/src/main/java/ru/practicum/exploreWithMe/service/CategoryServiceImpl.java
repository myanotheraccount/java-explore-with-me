package ru.practicum.exploreWithMe.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exploreWithMe.dto.CategoryDto;
import ru.practicum.exploreWithMe.exception.ConflictException;
import ru.practicum.exploreWithMe.exception.NotFoundException;
import ru.practicum.exploreWithMe.mapper.CategoryMapper;
import ru.practicum.exploreWithMe.model.Category;
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
        log.info(String.format("сохранение категории: %s", categoryDto.getName()));
        try {
            Category category = categoryRepository.saveAndFlush(CategoryMapper.fromDto(categoryDto));
            return CategoryMapper.toDto(category);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("имя категории не уникально");
        }
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
        log.info(String.format("обновление категории %d", categoryDto.getId()));
        try {
            Category category = categoryRepository.saveAndFlush(CategoryMapper.fromDto(categoryDto));
            return CategoryMapper.toDto(category);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("имя категории не уникально");
        }
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        log.info(String.format("удаление категории %d", id));
        categoryRepository.deleteById(id);
    }
}
