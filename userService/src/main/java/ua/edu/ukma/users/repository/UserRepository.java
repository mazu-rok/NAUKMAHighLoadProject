package ua.edu.ukma.users.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.edu.ukma.users.model.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    void deleteByUsername(String username);
}