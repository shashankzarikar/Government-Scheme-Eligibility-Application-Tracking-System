package com.government.scheme_management.service;

import com.government.scheme_management.dto.DashboardStats;
import com.government.scheme_management.dto.UserResponse;
import com.government.scheme_management.entity.Application;
import com.government.scheme_management.entity.Role;
import com.government.scheme_management.entity.User;
import com.government.scheme_management.repository.ApplicationRepository;
import com.government.scheme_management.repository.RoleRepository;
import com.government.scheme_management.repository.SchemeRepository;
import com.government.scheme_management.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private SchemeRepository schemeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ── Get admin dashboard stats ────────────────────────────────────────────
    public DashboardStats getDashboardStats() {
        DashboardStats stats = new DashboardStats();
        stats.setTotalUsers(userRepository.count());
        stats.setTotalApplications(applicationRepository.count());
        stats.setActiveSchemes(schemeRepository.findByIsActive(true).size());
        stats.setPendingApplications(applicationRepository.countByStatus(Application.Status.PENDING));
        stats.setApprovedApplications(applicationRepository.countByStatus(Application.Status.APPROVED));
        stats.setRejectedApplications(applicationRepository.countByStatus(Application.Status.REJECTED));
        return stats;
    }

    // ── Get all users ────────────────────────────────────────────────────────
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::toUserResponse)
                .collect(Collectors.toList());
    }

    // ── Get all officers only - for assign officer dropdown ──────────────────
    public List<UserResponse> getAllOfficers() {
        return userRepository.findByRole_RoleName("OFFICER")
                .stream()
                .map(this::toUserResponse)
                .collect(Collectors.toList());
    }

    // ── Add a new user (officer or admin) from admin panel ───────────────────
    public UserResponse addUser(String fullName, String email, String password,
                                String phone, String roleName) {

        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already registered: " + email);
        }

        Role role = roleRepository.findByRoleName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));

        User user = new User();
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setPhone(phone);
        user.setRole(role);
        user.setIsActive(true);

        return toUserResponse(userRepository.save(user));
    }

    // ── Deactivate a user (soft delete) ─────────────────────────────────────
    public String deactivateUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        user.setIsActive(false);
        userRepository.save(user);
        return "User deactivated successfully";
    }

    // ── Reactivate a user ────────────────────────────────────────────────────
    public String activateUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        user.setIsActive(true);
        userRepository.save(user);
        return "User activated successfully";
    }

    // ── Helper: User entity → UserResponse ──────────────────────────────────
    private UserResponse toUserResponse(User user) {
        UserResponse res = new UserResponse();
        res.setUserId(user.getUserId());
        res.setFullName(user.getFullName());
        res.setEmail(user.getEmail());
        res.setPhone(user.getPhone());
        res.setRole(user.getRole().getRoleName());
        res.setIsActive(user.getIsActive());
        res.setCreatedAt(user.getCreatedAt());
        return res;
    }
}
