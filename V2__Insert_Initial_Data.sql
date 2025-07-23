INSERT INTO modules (module, display_name, description, created_by, sort_order) VALUES
('RSA_MODULE', 'RSA Module', 'Risk and Security Assessment Module', 'SYSTEM', 1),
('USER_MANAGEMENT', 'User Management', 'User and Role Management Module', 'SYSTEM', 2);

-- Insert Roles
INSERT INTO roles (role, display_name, role_type, description, created_by) VALUES
('SUPER_ADMIN', 'Super Administrator', 'SUPER_ADMIN', 'Full system access', 'SYSTEM'),
('ADMIN', 'Administrator', 'ADMIN', 'Administrative access', 'SYSTEM'),
('RSA_MANAGER', 'RSA Manager', 'MANAGEMENT', 'RSA Module management access', 'SYSTEM');

-- Insert Permissions for RSA Module
INSERT INTO permissions (module_id, action, permission, display_name, created_by) VALUES
((SELECT id FROM modules WHERE module = 'RSA_MODULE'), 'read', 'RSA_READ', 'Read RSA Module', 'SYSTEM'),
((SELECT id FROM modules WHERE module = 'RSA_MODULE'), 'WRITE', 'RSA_WRITE', 'Write RSA Module', 'SYSTEM'),
((SELECT id FROM modules WHERE module = 'RSA_MODULE'), 'EDIT', 'RSA_EDIT', 'Edit RSA Module', 'SYSTEM'),
((SELECT id FROM modules WHERE module = 'RSA_MODULE'), 'DELETE', 'RSA_DELETE', 'Delete RSA Module', 'SYSTEM');

-- Insert Permissions for User Management Module
INSERT INTO permissions (module_id, action, permission, display_name, created_by) VALUES
((SELECT id FROM modules WHERE module = 'USER_MANAGEMENT'), 'read', 'USER_READ', 'Read User Management', 'SYSTEM'),
((SELECT id FROM modules WHERE module = 'USER_MANAGEMENT'), 'WRITE', 'USER_WRITE', 'Write User Management', 'SYSTEM'),
((SELECT id FROM modules WHERE module = 'USER_MANAGEMENT'), 'EDIT', 'USER_EDIT', 'Edit User Management', 'SYSTEM'),
((SELECT id FROM modules WHERE module = 'USER_MANAGEMENT'), 'DELETE', 'USER_DELETE', 'Delete User Management', 'SYSTEM');

-- Assign Permissions to Roles

-- Super Admin - All permissions (Full access to everything)
INSERT INTO role_permissions (role_id, permission_id, created_by) VALUES
-- RSA Module permissions for Super Admin
((SELECT id FROM roles WHERE role = 'SUPER_ADMIN'), (SELECT id FROM permissions WHERE permission = 'RSA_READ'), 'SYSTEM'),
((SELECT id FROM roles WHERE role = 'SUPER_ADMIN'), (SELECT id FROM permissions WHERE permission = 'RSA_WRITE'), 'SYSTEM'),
((SELECT id FROM roles WHERE role = 'SUPER_ADMIN'), (SELECT id FROM permissions WHERE permission = 'RSA_EDIT'), 'SYSTEM'),
((SELECT id FROM roles WHERE role = 'SUPER_ADMIN'), (SELECT id FROM permissions WHERE permission = 'RSA_DELETE'), 'SYSTEM'),
-- User Management permissions for Super Admin
((SELECT id FROM roles WHERE role = 'SUPER_ADMIN'), (SELECT id FROM permissions WHERE permission = 'USER_READ'), 'SYSTEM'),
((SELECT id FROM roles WHERE role = 'SUPER_ADMIN'), (SELECT id FROM permissions WHERE permission = 'USER_WRITE'), 'SYSTEM'),
((SELECT id FROM roles WHERE role = 'SUPER_ADMIN'), (SELECT id FROM permissions WHERE permission = 'USER_EDIT'), 'SYSTEM'),
((SELECT id FROM roles WHERE role = 'SUPER_ADMIN'), (SELECT id FROM permissions WHERE permission = 'USER_DELETE'), 'SYSTEM');

-- Admin - User management + Read RSA (Can manage users and view RSA)
INSERT INTO role_permissions (role_id, permission_id, created_by) VALUES
-- RSA Module read permission for Admin
((SELECT id FROM roles WHERE role = 'ADMIN'), (SELECT id FROM permissions WHERE permission = 'RSA_READ'), 'SYSTEM'),
-- User Management permissions for Admin (can create users)
((SELECT id FROM roles WHERE role = 'ADMIN'), (SELECT id FROM permissions WHERE permission = 'USER_READ'), 'SYSTEM'),
((SELECT id FROM roles WHERE role = 'ADMIN'), (SELECT id FROM permissions WHERE permission = 'USER_WRITE'), 'SYSTEM'),
((SELECT id FROM roles WHERE role = 'ADMIN'), (SELECT id FROM permissions WHERE permission = 'USER_EDIT'), 'SYSTEM');

-- RSA Manager - RSA Module permissions only (Can manage RSA but not users)
INSERT INTO role_permissions (role_id, permission_id, created_by) VALUES
((SELECT id FROM roles WHERE role = 'RSA_MANAGER'), (SELECT id FROM permissions WHERE permission = 'RSA_READ'), 'SYSTEM'),
((SELECT id FROM roles WHERE role = 'RSA_MANAGER'), (SELECT id FROM permissions WHERE permission = 'RSA_WRITE'), 'SYSTEM'),
((SELECT id FROM roles WHERE role = 'RSA_MANAGER'), (SELECT id FROM permissions WHERE permission = 'RSA_EDIT'), 'SYSTEM');

-- Create Super Admin User
-- IMPORTANT: Password hash for 'SuperAdmin@123' using BCrypt (strength 10)
-- This hash was generated using: new BCryptPasswordEncoder().encode("SuperAdmin@123")
-- You can generate a new hash using the PasswordGeneratorUtil.java utility
INSERT INTO users (email, name, password, user_type, user_group, channel, organization, created_by, must_change_password) VALUES
('superadmin@example.com', 'Super Administrator', '$2a$10$XptfkjlBXW5AJhOZfgqnfOD8DPJ5Zp4Lw0P5Z8qJZF7BzR7KpQFVy', 'INTERNAL', 'ADMIN_GROUP', 'WEB', 'SYSTEM_ORG', 'SYSTEM', false);

-- Assign Super Admin role to Super Admin user
INSERT INTO user_roles (user_id, role_id, assigned_by) VALUES
((SELECT id FROM users WHERE email = 'superadmin@example.com'), (SELECT id FROM roles WHERE role = 'SUPER_ADMIN'), 'SYSTEM');