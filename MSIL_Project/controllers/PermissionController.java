package com.MSIL.MSIL_Project.controllers;

import com.MSIL.MSIL_Project.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/permissions")
@CrossOrigin(origins = "*")
public class PermissionController {

    @Autowired
    private AuthService authService;

    @PostMapping("/check")
    public ResponseEntity<?> checkPermission(@RequestBody Map<String, String> request,
                                             Authentication authentication) {
        try {
            String moduleName = request.get("module");
            String action = request.get("action");
            String email = authentication.getName();

            boolean hasPermission = authService.hasPermission(email, moduleName, action);

            return ResponseEntity.ok().body(Map.of(
                    "hasPermission", hasPermission,
                    "module", moduleName,
                    "action", action,
                    "user", email
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}
