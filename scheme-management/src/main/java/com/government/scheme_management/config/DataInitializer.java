package com.government.scheme_management.config;

import com.government.scheme_management.entity.Role;
import com.government.scheme_management.entity.User;
import com.government.scheme_management.repository.RoleRepository;
import com.government.scheme_management.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

// Runs once on every application startup
// Creates the 3 roles and a default admin user if they don't exist
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        // ── Seed Roles ───────────────────────────────────────────────────────
        // Only insert if they don't already exist
        seedRole("APPLICANT");
        seedRole("OFFICER");
        seedRole("ADMIN");

        System.out.println("✓ Roles seeded: APPLICANT, OFFICER, ADMIN");

        // ── Seed / repair built-in admin and officer accounts ─────────────────
        // These accounts are used for dashboard access, so we keep them in sync
        // on every startup to avoid stale database rows breaking login.
        upsertUser(
                "admin@gov.in",
                "Admin User",
                "admin123",
                "9000000001",
                "Government Office, New Delhi",
                "ADMIN"
        );

        upsertUser(
                "ramesh@gov.in",
                "Officer Ramesh",
                "officer123",
                "9000000002",
                "Government Office, Mumbai",
                "OFFICER"
        );

        System.out.println("✓ DataInitializer complete. System ready.");
    }

    // Helper: insert role only if it doesn't already exist
    private void seedRole(String roleName) {
        if (roleRepository.findByRoleName(roleName).isEmpty()) {
            Role role = new Role();
            role.setRoleName(roleName);
            roleRepository.save(role);
        }
    }

    // Helper: create or repair a built-in user so login always works even if
    // the database already contains an older/stale record for that email.
    private void upsertUser(String email,
                            String fullName,
                            String rawPassword,
                            String phone,
                            String address,
                            String roleName) {

        Role role = roleRepository.findByRoleName(roleName)
                .orElseThrow(() -> new RuntimeException(roleName + " role not found"));

        User user = userRepository.findByEmail(email).orElseGet(User::new);
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setPhone(phone);
        user.setAddress(address);
        user.setRole(role);
        user.setIsActive(true);

        userRepository.save(user);
        System.out.println("✓ Built-in account ready: " + email + " / " + rawPassword);
    }
}
