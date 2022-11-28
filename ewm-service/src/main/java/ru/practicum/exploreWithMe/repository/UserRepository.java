package ru.practicum.exploreWithMe.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.exploreWithMe.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByName(String name);
}
