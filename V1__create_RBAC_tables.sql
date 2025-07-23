CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    auth_user_id VARCHAR(255) UNIQUE,
    auth_provider VARCHAR(255),
    name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(20),
    user_type VARCHAR(20) NOT NULL CHECK (user_type IN ('INTERNAL', 'EXTERNAL')),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'INACTIVE', 'SUSPENDED')),
    user_group VARCHAR(100) NOT NULL,
    channel VARCHAR(100) NOT NULL,
    organization VARCHAR(100) NOT NULL,
    last_login TIMESTAMP,
    password_changed_at TIMESTAMP,
    must_change_password BOOLEAN DEFAULT TRUE,
    password VARCHAR(255) NOT NULL,  -- IMPORTANT: Added password column
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

-- Create Modules table
CREATE TABLE modules (
    id BIGSERIAL PRIMARY KEY,  -- FIXED: BIGSERIAL instead of SERIAL
    module VARCHAR(100) UNIQUE NOT NULL,
    display_name VARCHAR(100) NOT NULL,
    description TEXT,
    module_path VARCHAR(255),
    icon VARCHAR(100),
    sort_order INTEGER DEFAULT 0,  -- FIXED: 0 instead of O (letter O)
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    parent_module_id BIGINT REFERENCES modules(id),  -- FIXED: BIGINT for foreign key
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

-- Create Roles table
CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,  -- FIXED: BIGSERIAL instead of SERIAL
    role VARCHAR(100) UNIQUE NOT NULL,
    display_name VARCHAR(100) NOT NULL,
    description TEXT,
    role_type VARCHAR(20) NOT NULL CHECK (role_type IN ('SUPER_ADMIN', 'ADMIN', 'MANAGEMENT', 'BUSINESS', 'PARTNER', 'OTHER')),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

-- Create Permissions table
CREATE TABLE permissions (
    id BIGSERIAL PRIMARY KEY,  -- FIXED: BIGSERIAL instead of SERIAL
    module_id BIGINT NOT NULL REFERENCES modules(id),  -- FIXED: BIGINT for foreign key
    action VARCHAR(20) NOT NULL CHECK (action IN ('read', 'WRITE', 'EDIT', 'DELETE', 'DOWNLOAD', 'UPLOAD')),  -- FIXED: Correct case
    permission VARCHAR(100) NOT NULL,
    display_name VARCHAR(100) NOT NULL,
    description TEXT,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    UNIQUE(module_id, action)  -- Unique constraint for module-action combination
);

-- Create User Roles junction table
CREATE TABLE user_roles (
    id BIGSERIAL PRIMARY KEY,  -- FIXED: BIGSERIAL instead of SERIAL
    user_id BIGINT NOT NULL REFERENCES users(id),
    role_id BIGINT NOT NULL REFERENCES roles(id),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    assigned_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP,
    assigned_by VARCHAR(255) NOT NULL,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(255),
    UNIQUE(user_id, role_id)  -- Unique constraint for user-role combination
);

-- Create Role Permissions junction table
CREATE TABLE role_permissions (
    id BIGSERIAL PRIMARY KEY,  -- FIXED: BIGSERIAL instead of SERIAL
    role_id BIGINT NOT NULL REFERENCES roles(id),
    permission_id BIGINT NOT NULL REFERENCES permissions(id),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    UNIQUE(role_id, permission_id)  -- Unique constraint for role-permission combination
);

-- Create indexes for better performance
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_auth_user_id ON users(auth_user_id);
CREATE INDEX idx_users_status ON users(status);
CREATE INDEX idx_users_user_type ON users(user_type);

CREATE INDEX idx_modules_parent_id ON modules(parent_module_id);
CREATE INDEX idx_modules_is_active ON modules(is_active);

CREATE INDEX idx_roles_role_type ON roles(role_type);
CREATE INDEX idx_roles_is_active ON roles(is_active);

CREATE INDEX idx_permissions_module_id ON permissions(module_id);
CREATE INDEX idx_permissions_action ON permissions(action);

CREATE INDEX idx_user_roles_user_id ON user_roles(user_id);
CREATE INDEX idx_user_roles_role_id ON user_roles(role_id);
CREATE INDEX idx_user_roles_is_active ON user_roles(is_active);

CREATE INDEX idx_role_permissions_role_id ON role_permissions(role_id);  -- FIXED: role_id instead of role_ia
CREATE INDEX idx_role_permissions_permission_id ON role_permissions(permission_id);
CREATE INDEX idx_role_permissions_is_active ON role_permissions(is_active);  -- FIXED: Missing parenthesis

