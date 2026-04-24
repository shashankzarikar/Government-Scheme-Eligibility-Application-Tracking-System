package com.government.scheme_management.service;

import com.government.scheme_management.dto.LoginRequest;
import com.government.scheme_management.dto.LoginResponse;
import com.government.scheme_management.dto.RegisterRequest;
import com.government.scheme_management.entity.Role;
import com.government.scheme_management.entity.User;
import com.government.scheme_management.repository.RoleRepository;
import com.government.scheme_management.repository.UserRepository;
import com.government.scheme_management.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    // Register a new applicant
    public String register(RegisterRequest request) {

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered: " + request.getEmail());
        }

        // Always register as APPLICANT role
        Role applicantRole = roleRepository.findByRoleName("APPLICANT")
                .orElseThrow(() -> new RuntimeException("APPLICANT role not found. Run DataInitializer first."));

        // Build user entity
        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword())); // BCrypt hash
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());
        user.setDateOfBirth(request.getDateOfBirth());
        user.setGender(request.getGender());
        user.setRole(applicantRole);
        user.setIsActive(true);

        userRepository.save(user);
        return "Registration successful!";
    }

    // Login - authenticate and return JWT token
    public LoginResponse login(LoginRequest request) {

        // Authenticate using Spring Security (checks email + password)
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        // If authentication successful, load user from DB
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Generate JWT token with email and role
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().getRoleName());

        return new LoginResponse(token, user.getRole().getRoleName(), user.getFullName(), user.getUserId());
    }
}
