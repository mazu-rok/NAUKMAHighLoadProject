package ua.edu.ukma.users.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ua.edu.ukma.users.dto.auth.JwtAuthenticationResponse;
import ua.edu.ukma.users.dto.auth.SignInRequest;
import ua.edu.ukma.users.dto.auth.SignUpRequest;
import ua.edu.ukma.users.exception.InvalidCredentialsException;
import ua.edu.ukma.users.exception.InvalidJwtTokenException;
import ua.edu.ukma.users.exception.TokenRefreshException;
import ua.edu.ukma.users.exception.UserAlreadyExistsException;
import ua.edu.ukma.users.model.User;
import ua.edu.ukma.users.model.UserRole;
import ua.edu.ukma.users.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public JwtAuthenticationResponse signup(SignUpRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException("Username is already taken");
        }

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("Email is already registered");
        }

        var user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(UserRole.ROLE_USER);
        userRepository.save(user);

        var jwt = jwtService.generateToken(user);
        var refresh = jwtService.generateRefreshToken(user);

        return JwtAuthenticationResponse.builder()
                .id(user.getId())
                .accessToken(jwt)
                .refreshToken(refresh)
                .build();
    }

    public JwtAuthenticationResponse signin(SignInRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            throw new InvalidCredentialsException("Invalid username or password");
        }

        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid username or password"));

        var jwt = jwtService.generateToken(user);
        var refresh = jwtService.generateRefreshToken(user);

        return JwtAuthenticationResponse.builder()
                .id(user.getId())
                .accessToken(jwt)
                .refreshToken(refresh)
                .build();
    }

    public JwtAuthenticationResponse refreshToken(String refreshToken) {
        try {
            String username = jwtService.extractUsername(refreshToken);
            var user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new TokenRefreshException("Invalid refresh token"));

            if (!jwtService.isTokenValid(refreshToken, user)) {
                throw new TokenRefreshException("Refresh token is expired");
            }

            var newAccessToken = jwtService.generateToken(user);

            return JwtAuthenticationResponse.builder()
                    .id(user.getId())
                    .accessToken(newAccessToken)
                    .refreshToken(refreshToken)
                    .build();
        } catch (Exception e) {
            throw new InvalidJwtTokenException("Failed to process refresh token: " + e.getMessage());
        }
    }
}