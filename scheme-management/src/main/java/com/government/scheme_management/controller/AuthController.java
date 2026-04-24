package com.government.scheme_management.controller;

import com.government.scheme_management.dto.LoginRequest;
import com.government.scheme_management.dto.LoginResponse;
import com.government.scheme_management.dto.RegisterRequest;
import com.government.scheme_management.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*") // Allow frontend HTML pages to call this API
public class AuthController {

    @Autowired
    private AuthService authService;

    // POST /api/auth/register
    // Public - anyone can register as an applicant
    // Body: { fullName, email, password, phone, address, dateOfBirth, gender }
    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request) {
        String message = authService.register(request);
        return ResponseEntity.ok(message);
    }

    // POST /api/auth/login
    // Public - returns JWT token on success
    // Body: { email, password }
    // Response: { token, role, fullName, userId }
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
