package ru.practicum.exploreWithMe.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.exploreWithMe.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
