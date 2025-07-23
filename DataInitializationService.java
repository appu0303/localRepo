package com.MSIL.MSIL_Project.service;

import com.MSIL.MSIL_Project.entity.*;
import com.MSIL.MSIL_Project.entity.Module;
import com.MSIL.MSIL_Project.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@ConditionalOnProperty(name = "app.data-initialization.enabled", havingValue = "true", matchIfMissing = true)
@Transactional
public class DataInitializationService implements CommandLineRunner {

    @Value("${app.data-initialization.enabled:true}")
    private boolean dataInitializationEnabled;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ModuleRepository moduleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private RolePermissionRepository rolePermissionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (dataInitializationEnabled) {
            System.out.println("🚀 === JPA Data Initialization Enabled ===");
            initializeData();
        } else {
            System.out.println("🔄 === Data Initialization Disabled - Using Flyway Migrations ===");
            // Just verify that Super Admin exists from Flyway migration
            checkSuperAdminExists();
        }
    }

    private void checkSuperAdminExists() {
        String superAdminEmail = "superadmin@example.com";
        if (userRepository.existsByEmail(superAdminEmail)) {
            System.out.println("✅ === SUPER ADMIN EXISTS (Created by Flyway) ===");
            System.out.println("📧 Email: " + superAdminEmail);
            System.out.println("🔑 Password: SuperAdmin@123");
            System.out.println("📊 Total users in database: " + userRepository.count());
            System.out.println("📊 Total roles in database: " + roleRepository.count());
            System.out.println("📊 Total modules in database: " + moduleRepository.count());
            System.out.println("============================================");
        } else {
            System.err.println("⚠️  WARNING: Super Admin user not found!");
            System.err.println("🔍 Check Flyway migration V2__Insert_Initial_Data.sql");
            System.err.println("📊 Current user count: " + userRepository.count());
        }
    }

    // ORIGINAL JPA INITIALIZATION METHOD (kept for when Flyway is disabled)
    private void initializeData() {
        System.out.println("📝 Starting JPA-based data initialization...");

        // Create Modules
        Module rsaModule = createModuleIfNotExists("RSA_MODULE", "RSA Module", "Risk and Security Assessment Module");
        Module userManagementModule = createModuleIfNotExists("USER_MANAGEMENT", "User Management", "User and Role Management Module");

        // Create Roles
        Role superAdminRole = createRoleIfNotExists("SUPER_ADMIN", "Super Administrator", RoleType.SUPER_ADMIN, "Full system access");
        Role adminRole = createRoleIfNotExists("ADMIN", "Administrator", RoleType.ADMIN, "Administrative access");
        Role rsaManagerRole = createRoleIfNotExists("RSA_MANAGER", "RSA Manager", RoleType.MANAGEMENT, "RSA Module management access");

        // Create Permissions
        Permission rsaRead = createPermissionIfNotExists(rsaModule, ActionType.READ, "RSA_READ", "Read RSA Module");
        Permission rsaWrite = createPermissionIfNotExists(rsaModule, ActionType.WRITE, "RSA_WRITE", "Write RSA Module");
        Permission rsaEdit = createPermissionIfNotExists(rsaModule, ActionType.EDIT, "RSA_EDIT", "Edit RSA Module");
        Permission rsaDelete = createPermissionIfNotExists(rsaModule, ActionType.DELETE, "RSA_DELETE", "Delete RSA Module");

        Permission userRead = createPermissionIfNotExists(userManagementModule, ActionType.READ, "USER_READ", "Read User Management");
        Permission userWrite = createPermissionIfNotExists(userManagementModule, ActionType.WRITE, "USER_WRITE", "Write User Management");
        Permission userEdit = createPermissionIfNotExists(userManagementModule, ActionType.EDIT, "USER_EDIT", "Edit User Management");
        Permission userDelete = createPermissionIfNotExists(userManagementModule, ActionType.DELETE, "USER_DELETE", "Delete User Management");

        // Assign Permissions to Roles
        // Super Admin - All permissions
        assignPermissionToRole(superAdminRole, rsaRead, "SYSTEM");
        assignPermissionToRole(superAdminRole, rsaWrite, "SYSTEM");
        assignPermissionToRole(superAdminRole, rsaEdit, "SYSTEM");
        assignPermissionToRole(superAdminRole, rsaDelete, "SYSTEM");
        assignPermissionToRole(superAdminRole, userRead, "SYSTEM");
        assignPermissionToRole(superAdminRole, userWrite, "SYSTEM");
        assignPermissionToRole(superAdminRole, userEdit, "SYSTEM");
        assignPermissionToRole(superAdminRole, userDelete, "SYSTEM");

        // Admin - User management + Read RSA
        assignPermissionToRole(adminRole, rsaRead, "SYSTEM");
        assignPermissionToRole(adminRole, userRead, "SYSTEM");
        assignPermissionToRole(adminRole, userWrite, "SYSTEM");
        assignPermissionToRole(adminRole, userEdit, "SYSTEM");

        // RSA Manager - RSA Module permissions
        assignPermissionToRole(rsaManagerRole, rsaRead, "SYSTEM");
        assignPermissionToRole(rsaManagerRole, rsaWrite, "SYSTEM");
        assignPermissionToRole(rsaManagerRole, rsaEdit, "SYSTEM");

        // Create Super Admin User
        createSuperAdminIfNotExists();

        System.out.println("✅ JPA data initialization completed successfully!");
    }

    private Module createModuleIfNotExists(String module, String displayName, String description) {
        return moduleRepository.findByModule(module)
                .orElseGet(() -> {
                    Module newModule = new Module(module, displayName, description);
                    newModule.setCreatedBy("SYSTEM");
                    return moduleRepository.save(newModule);
                });
    }

    private Role createRoleIfNotExists(String role, String displayName, RoleType roleType, String description) {
        return roleRepository.findByRole(role)
                .orElseGet(() -> {
                    Role newRole = new Role(role, displayName, roleType, description);
                    newRole.setCreatedBy("SYSTEM");
                    return roleRepository.save(newRole);
                });
    }

    private Permission createPermissionIfNotExists(Module module, ActionType action, String permission, String displayName) {
        return permissionRepository.findByModuleAndAction(module, action)
                .orElseGet(() -> {
                    Permission newPermission = new Permission(module, action, permission, displayName);
                    newPermission.setCreatedBy("SYSTEM");
                    return permissionRepository.save(newPermission);
                });
    }

    private void assignPermissionToRole(Role role, Permission permission, String assignedBy) {
        if (!rolePermissionRepository.findByRoleAndPermission(role, permission).isPresent()) {
            RolePermission rolePermission = new RolePermission(role, permission, assignedBy);
            rolePermissionRepository.save(rolePermission);
        }
    }

    private void createSuperAdminIfNotExists() {
        String superAdminEmail = "superadmin@example.com";

        if (!userRepository.existsByEmail(superAdminEmail)) {
            User superAdmin = new User(
                    superAdminEmail,
                    "Super Administrator",
                    passwordEncoder.encode("SuperAdmin@123"),
                    UserType.INTERNAL,
                    "ADMIN_GROUP",
                    "WEB",
                    "SYSTEM_ORG"
            );
            superAdmin.setCreatedBy("SYSTEM");
            superAdmin.setMustChangePassword(false);

            User savedUser = userRepository.save(superAdmin);

            Role superAdminRole = roleRepository.findByRole("SUPER_ADMIN")
                    .orElseThrow(() -> new RuntimeException("Super Admin role not found"));

            UserRole userRole = new UserRole(savedUser, superAdminRole, "SYSTEM");
            userRoleRepository.save(userRole);

            System.out.println("✅ === SUPER ADMIN CREATED (JPA) ===");
            System.out.println("📧 Email: " + superAdminEmail);
            System.out.println("🔑 Password: SuperAdmin@123");
            System.out.println("================================");
        }
    }
}