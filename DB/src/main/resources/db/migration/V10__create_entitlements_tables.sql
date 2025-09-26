-- V10: Create roles and user_roles tables for entitlements

-- Roles Table
CREATE TABLE eShop.roles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(50) UNIQUE NOT NULL, -- e.g., 'USER', 'ADMIN'
    created_timestamp TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    last_updated_timestamp TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES eShop.users(id),
    last_updated_by UUID REFERENCES eShop.users(id)
);

-- User-Roles Join Table
CREATE TABLE eShop.user_roles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES eShop.users(id) ON DELETE CASCADE,
    role_id UUID NOT NULL REFERENCES eShop.roles(id) ON DELETE CASCADE,
    created_timestamp TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    last_updated_timestamp TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES eShop.users(id),
    last_updated_by UUID REFERENCES eShop.users(id),
    UNIQUE(user_id, role_id)
);

-- Pre-populate the standard roles
-- Note: The created_by for these initial roles will be NULL as they are system-defined.
INSERT INTO eShop.roles (name) VALUES ('USER'), ('ADMIN');
