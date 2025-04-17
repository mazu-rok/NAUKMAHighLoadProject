package ua.edu.ukma.users.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ua.edu.ukma.users.exception.UserNotFoundException;
import ua.edu.ukma.users.model.User;
import ua.edu.ukma.users.model.UserRole;
import ua.edu.ukma.users.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User findByUsernameOrThrow(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));
    }

    public Optional<User> findById(UUID id) {
        return userRepository.findById(id);
    }

    public User findByIdOrThrow(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public User createUser(String username, String email, String rawPassword, UserRole role) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRole(role);
        return userRepository.save(user);
    }

    public User updateUser(User user) {
        return userRepository.save(user);
    }

    public void deleteByUsername(String username) {
        userRepository.deleteByUsername(username);
    }

    public void deleteById(UUID id) {
        userRepository.deleteById(id);
    }
}
