package ua.edu.ukma.users.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import ua.edu.ukma.users.model.User;
import ua.edu.ukma.users.model.UserRole;
import ua.edu.ukma.users.repository.UserRepository;

@Configuration
@RequiredArgsConstructor
public class AdminInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.username}")
    private String adminUsername;

    @Value("${admin.password}")
    private String adminPassword;

    @Value("${admin.email}")
    private String adminEmail;

    @Bean
    public CommandLineRunner createAdminUser() {
        return args -> {
            if (!userRepository.existsByUsername(adminUsername)) {
                User adminUser = new User();
                adminUser.setUsername(adminUsername);
                adminUser.setEmail(adminEmail);
                adminUser.setPassword(passwordEncoder.encode(adminPassword));
                adminUser.setRole(UserRole.ROLE_ADMIN);
                userRepository.save(adminUser);
            }
        };
    }
}