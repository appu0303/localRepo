package com.MSIL.MSIL_Project.controllers;

import com.MSIL.MSIL_Project.dtos.UserRegistrationRequest;
import com.MSIL.MSIL_Project.entity.User;
import com.MSIL.MSIL_Project.security.CustomUserDetails;
import com.MSIL.MSIL_Project.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Authentication service is working! Use basic authentication with email and password.");
    }

    @GetMapping("/me")
    @Transactional(readOnly = true)  // Ensures active transaction
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        try {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            User user = userDetails.getUser();

            return ResponseEntity.ok().body(Map.of(
                    "email", user.getEmail(),
                    "name", user.getName(),
                    "userType", user.getUserType().name(),
                    "status", user.getStatus().name(),
                    "permissions", userDetails.getPermissions(),
                    "message", "Successfully authenticated with Basic Authentication"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/register")
    @PreAuthorize("hasAuthority('USER_MANAGEMENT:WRITE')")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRegistrationRequest registrationRequest,
                                          Authentication authentication) {
        try {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            String createdBy = userDetails.getUser().getEmail();

            User user = authService.registerUser(registrationRequest, createdBy);
            return ResponseEntity.ok().body(Map.of(
                    "message", "User registered successfully",
                    "userEmail", user.getEmail(),
                    "userName", user.getName(),
                    "createdBy", createdBy
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}