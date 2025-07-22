package com.MSIL.MSIL_Project.service;

import com.MSIL.MSIL_Project.entity.User;
import com.MSIL.MSIL_Project.repository.PermissionRepository;
import com.MSIL.MSIL_Project.repository.UserRepository;
import com.MSIL.MSIL_Project.security.CustomUserDetails;

import com.MSIL.MSIL_Project.repository.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        // Update last login time in a separate transaction
        updateLastLogin(email);

        // Get permissions using separate repository call to avoid lazy loading
        List<String> permissions = permissionRepository.findByUserId(user.getId())
                .stream()
                .map(permission -> permission.getModule().getModule() + ":" + permission.getAction().name())
                .collect(Collectors.toList());

        // Get role-based authorities using separate repository call
        List<String> roleAuthorities = userRoleRepository.findActiveRolesByUserId(user.getId())
                .stream()
                .map(userRole -> userRole.getRole().getRole())
                .collect(Collectors.toList());

        permissions.addAll(roleAuthorities);

        return new CustomUserDetails(user, permissions);
    }

    @Transactional
    public void updateLastLogin(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);
    }
}