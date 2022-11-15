package ru.practicum.exploreWithMe.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.exploreWithMe.models.User;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByName(String name);
}
