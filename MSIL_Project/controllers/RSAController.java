package com.MSIL.MSIL_Project.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rsa")
@CrossOrigin(origins = "*")
public class RSAController {

    @GetMapping("/dashboard")
    @PreAuthorize("hasAuthority('RSA_MODULE:READ')")
    public ResponseEntity<?> getRSADashboard(Authentication authentication) {
        try {
            return ResponseEntity.ok().body(Map.of(
                    "message", "Welcome to RSA Module Dashboard",
                    "user", authentication.getName(),
                    "data", List.of(
                            Map.of("id", 1, "title", "Risk Assessment 1", "status", "Completed"),
                            Map.of("id", 2, "title", "Security Audit 1", "status", "In Progress"),
                            Map.of("id", 3, "title", "Compliance Check 1", "status", "Pending")
                    )
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/assessment")
    @PreAuthorize("hasAuthority('RSA_MODULE:WRITE')")
    public ResponseEntity<?> createAssessment(@RequestBody Map<String, Object> assessment,
                                              Authentication authentication) {
        try {
            // Simulate creating a new assessment
            return ResponseEntity.ok().body(Map.of(
                    "message", "Assessment created successfully",
                    "assessmentId", System.currentTimeMillis(),
                    "createdBy", authentication.getName(),
                    "data", assessment
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PutMapping("/assessment/{id}")
    @PreAuthorize("hasAuthority('RSA_MODULE:EDIT')")
    public ResponseEntity<?> updateAssessment(@PathVariable Long id,
                                              @RequestBody Map<String, Object> assessment,
                                              Authentication authentication) {
        try {
            return ResponseEntity.ok().body(Map.of(
                    "message", "Assessment updated successfully",
                    "assessmentId", id,
                    "updatedBy", authentication.getName(),
                    "data", assessment
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @DeleteMapping("/assessment/{id}")
    @PreAuthorize("hasAuthority('RSA_MODULE:DELETE')")
    public ResponseEntity<?> deleteAssessment(@PathVariable Long id, Authentication authentication) {
        try {
            return ResponseEntity.ok().body(Map.of(
                    "message", "Assessment deleted successfully",
                    "assessmentId", id,
                    "deletedBy", authentication.getName()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/reports")
    @PreAuthorize("hasAuthority('RSA_MODULE:READ')")
    public ResponseEntity<?> getReports() {
        try {
            return ResponseEntity.ok().body(Map.of(
                    "message", "RSA Reports",
                    "reports", List.of(
                            Map.of("id", 1, "name", "Monthly Security Report", "date", "2025-01-15"),
                            Map.of("id", 2, "name", "Risk Assessment Summary", "date", "2025-01-10"),
                            Map.of("id", 3, "name", "Compliance Status Report", "date", "2025-01-05")
                    )
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}
