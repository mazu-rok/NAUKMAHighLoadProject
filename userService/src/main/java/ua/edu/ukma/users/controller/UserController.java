package ua.edu.ukma.users.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ua.edu.ukma.users.dto.user.UserProfileResponse;
import ua.edu.ukma.users.model.User;
import ua.edu.ukma.users.service.UserService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<UserProfileResponse> getUserById(@PathVariable UUID id) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User authenticatedUser = userService.findByUsernameOrThrow(currentUsername);

        if (!authenticatedUser.getId().equals(id)) {
            return ResponseEntity.status(403).build();
        }

        User requestedUser = userService.findByIdOrThrow(id);

        return ResponseEntity.ok(
                UserProfileResponse.builder()
                        .id(requestedUser.getId())
                        .username(requestedUser.getUsername())
                        .email(requestedUser.getEmail())
                        .role(requestedUser.getRole().name())
                        .build()
        );
    }
}
