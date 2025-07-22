package com.MSIL.MSIL_Project.service;

import com.MSIL.MSIL_Project.dtos.UserRegistrationRequest;
import com.MSIL.MSIL_Project.entity.*;
import com.MSIL.MSIL_Project.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User registerUser(UserRegistrationRequest request, String createdBy) {
        // Check if user already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("User with email " + request.getEmail() + " already exists");
        }

        // Validate role exists
        if (request.getRoleName() == null || request.getRoleName().trim().isEmpty()) {
            throw new RuntimeException("Role name is required");
        }

        Role role = roleRepository.findByRole(request.getRoleName())
                .orElseThrow(() -> new RuntimeException("Role not found: " + request.getRoleName()));

        // Create new user
        User user = new User(
                request.getEmail(),
                request.getName(),
                passwordEncoder.encode(request.getPassword()),
                request.getUserType(),
                request.getUserGroup(),
                request.getChannel(),
                request.getOrganization()
        );

        user.setPhoneNumber(request.getPhoneNumber());
        user.setCreatedBy(createdBy);
        user.setPasswordChangedAt(LocalDateTime.now());
        user.setMustChangePassword(false); // Set to false for admin-created users

        User savedUser = userRepository.save(user);

        // Assign role to user
        UserRole userRole = new UserRole(savedUser, role, createdBy);
        userRoleRepository.save(userRole);

        return savedUser;
    }

    public boolean hasPermission(String email, String moduleName, String action) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return permissionRepository.findByUserId(user.getId())
                .stream()
                .anyMatch(permission ->
                        permission.getModule().getModule().equals(moduleName) &&
                                permission.getAction().name().equalsIgnoreCase(action)
                );
    }

    public void updateLastLogin(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);
    }
}