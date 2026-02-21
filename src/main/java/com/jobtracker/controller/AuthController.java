package com.jobtracker.controller;

import com.jobtracker.dto.AuthRequestDTO;
import com.jobtracker.dto.AuthResponseDTO;
import com.jobtracker.dto.RegistrationRequestDTO;
import com.jobtracker.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Entry point for Authentication.
 * Handles user sign-up and sign-in requests.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // POST /api/auth/register - Create a new account
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody RegistrationRequestDTO request) {
        return ResponseEntity.ok(authService.register(request));
    }

    // POST /api/auth/login - Get a JWT token
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody AuthRequestDTO request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
