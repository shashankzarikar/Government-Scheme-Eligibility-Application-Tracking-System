package com.government.scheme_management.controller;

import com.government.scheme_management.dto.*;
import com.government.scheme_management.service.AdminService;
import com.government.scheme_management.service.ApplicationService;
import com.government.scheme_management.service.SchemeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

// All routes here require ROLE_ADMIN (enforced in SecurityConfig)
@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private SchemeService schemeService;

    // ── DASHBOARD ────────────────────────────────────────────────────────────

    // GET /api/admin/dashboard
    // Returns stats: total users, applications, pending count, etc.
    @GetMapping("/dashboard")
    public ResponseEntity<DashboardStats> getDashboardStats() {
        return ResponseEntity.ok(adminService.getDashboardStats());
    }

    // ── USER MANAGEMENT ──────────────────────────────────────────────────────

    // GET /api/admin/users
    // Returns list of all users
    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    // GET /api/admin/officers
    // Returns list of all officers - used in assign officer dropdown
    @GetMapping("/officers")
    public ResponseEntity<List<UserResponse>> getAllOfficers() {
        return ResponseEntity.ok(adminService.getAllOfficers());
    }

    // POST /api/admin/users
    // Add a new user (officer or admin) from admin panel
    // Body: { fullName, email, password, phone, role }
    @PostMapping("/users")
    public ResponseEntity<UserResponse> addUser(@RequestBody Map<String, String> body) {
        UserResponse user = adminService.addUser(
                body.get("fullName"),
                body.get("email"),
                body.get("password"),
                body.get("phone"),
                body.get("role")
        );
        return ResponseEntity.ok(user);
    }

    // PUT /api/admin/users/{userId}/deactivate
    // Soft-delete: sets is_active = false
    @PutMapping("/users/{userId}/deactivate")
    public ResponseEntity<String> deactivateUser(@PathVariable Integer userId) {
        return ResponseEntity.ok(adminService.deactivateUser(userId));
    }

    // PUT /api/admin/users/{userId}/activate
    // Re-activate a previously deactivated user
    @PutMapping("/users/{userId}/activate")
    public ResponseEntity<String> activateUser(@PathVariable Integer userId) {
        return ResponseEntity.ok(adminService.activateUser(userId));
    }

    // ── SCHEME MANAGEMENT ────────────────────────────────────────────────────

    // GET /api/admin/schemes
    // Returns all schemes (active + inactive)
    @GetMapping("/schemes")
    public ResponseEntity<List<SchemeResponse>> getAllSchemes() {
        return ResponseEntity.ok(schemeService.getAllSchemes());
    }

    // POST /api/admin/schemes
    // Create a new scheme
    // Body: { schemeName, description, eligibilityCriteria, requiredDocuments, startDate, endDate }
    @PostMapping("/schemes")
    public ResponseEntity<SchemeResponse> createScheme(
            @RequestBody SchemeRequest request,
            Authentication authentication) {
        // authentication.getName() returns the logged-in admin's email
        return ResponseEntity.ok(schemeService.createScheme(request, authentication.getName()));
    }

    // PUT /api/admin/schemes/{schemeId}
    // Update an existing scheme
    @PutMapping("/schemes/{schemeId}")
    public ResponseEntity<SchemeResponse> updateScheme(
            @PathVariable Integer schemeId,
            @RequestBody SchemeRequest request) {
        return ResponseEntity.ok(schemeService.updateScheme(schemeId, request));
    }

    // PUT /api/admin/schemes/{schemeId}/deactivate
    // Deactivate a scheme (no new applications accepted)
    @PutMapping("/schemes/{schemeId}/deactivate")
    public ResponseEntity<String> deactivateScheme(@PathVariable Integer schemeId) {
        return ResponseEntity.ok(schemeService.deactivateScheme(schemeId));
    }

    // DELETE /api/admin/schemes/{schemeId}
    // Permanently delete a scheme
    @DeleteMapping("/schemes/{schemeId}")
    public ResponseEntity<String> deleteScheme(@PathVariable Integer schemeId) {
        return ResponseEntity.ok(schemeService.deleteScheme(schemeId));
    }

    // ── APPLICATION MANAGEMENT ───────────────────────────────────────────────

    // GET /api/admin/applications
    // Returns all applications across all applicants and schemes
    @GetMapping("/applications")
    public ResponseEntity<List<ApplicationResponse>> getAllApplications() {
        return ResponseEntity.ok(applicationService.getAllApplications());
    }

    // GET /api/admin/applications/unassigned
    // Returns applications that have no officer assigned yet
    @GetMapping("/applications/unassigned")
    public ResponseEntity<List<ApplicationResponse>> getUnassignedApplications() {
        return ResponseEntity.ok(applicationService.getUnassignedApplications());
    }

    // PUT /api/admin/applications/{applicationId}/assign
    // Assign an officer to an application
    // Body: { officerId }
    @PutMapping("/applications/{applicationId}/assign")
    public ResponseEntity<ApplicationResponse> assignOfficer(
            @PathVariable Integer applicationId,
            @RequestBody AssignOfficerRequest request) {
        return ResponseEntity.ok(applicationService.assignOfficer(applicationId, request));
    }

    // GET /api/admin/applications/{applicationId}
    // Get full details of a single application
    @GetMapping("/applications/{applicationId}")
    public ResponseEntity<ApplicationResponse> getApplicationById(
            @PathVariable Integer applicationId,
            Authentication authentication) {
        return ResponseEntity.ok(applicationService.getApplicationById(applicationId, authentication.getName()));
    }
}
