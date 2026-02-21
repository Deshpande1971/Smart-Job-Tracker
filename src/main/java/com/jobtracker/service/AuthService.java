package com.jobtracker.service;

import com.jobtracker.dto.AuthRequestDTO;
import com.jobtracker.dto.AuthResponseDTO;
import com.jobtracker.dto.RegistrationRequestDTO;
import com.jobtracker.entity.User;
import com.jobtracker.exception.AlreadyExistsException;
import com.jobtracker.repository.UserRepository;
import com.jobtracker.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Handles Authentication Business Logic.
 * Responsible for User creation and Credential verification.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

        private final UserRepository userRepository;
        private final PasswordEncoder passwordEncoder;
        private final JwtUtils jwtUtils;
        private final AuthenticationManager authenticationManager;

        // Creates a new user and returns a token immediately.
        public AuthResponseDTO register(RegistrationRequestDTO request) {
                if (userRepository.existsByEmail(request.getEmail())) {
                        throw new AlreadyExistsException("Email already exists");
                }

                User user = User.builder()
                                .name(request.getName())
                                .email(request.getEmail())
                                .password(passwordEncoder.encode(request.getPassword())) // Secure hashing
                                .role(request.getRole())
                                .build();

                userRepository.save(user);

                String token = jwtUtils.generateToken(user);
                return mapToAuthResponse(user, token);
        }

        // Authenticates credentials and returns a fresh token.
        public AuthResponseDTO login(AuthRequestDTO request) {
                // Triggers the actual credential check
                authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

                User user = userRepository.findByEmail(request.getEmail())
                                .orElseThrow(() -> new RuntimeException("User not found"));

                String token = jwtUtils.generateToken(user);
                return mapToAuthResponse(user, token);
        }

        // Helper: Converts User entity to AuthResponseDTO
        private AuthResponseDTO mapToAuthResponse(User user, String token) {
                return AuthResponseDTO.builder()
                                .token(token)
                                .email(user.getEmail())
                                .role(user.getRole().name())
                                .build();
        }
}
