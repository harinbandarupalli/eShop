-- V1: Create eShop schema, roles, and user

CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- 1. Schema Creation
CREATE SCHEMA IF NOT EXISTS eShop;

-- 2. Roles and Permissions
DO
$$
BEGIN
   IF NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = 'read_role') THEN
      CREATE ROLE read_role;
   END IF;
   IF NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = 'write_role') THEN
      CREATE ROLE write_role;
   END IF;
END
$$;

GRANT CONNECT ON DATABASE eshop TO read_role;
GRANT USAGE ON SCHEMA eShop TO read_role;
GRANT SELECT ON ALL TABLES IN SCHEMA eShop TO read_role;
ALTER DEFAULT PRIVILEGES IN SCHEMA eShop GRANT SELECT ON TABLES TO read_role;

GRANT CONNECT ON DATABASE eshop TO write_role;
GRANT USAGE, CREATE ON SCHEMA eShop TO write_role;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA eShop TO write_role;
ALTER DEFAULT PRIVILEGES IN SCHEMA eShop GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO write_role;

-- 3. Application User
DO
$$
BEGIN
   IF NOT EXISTS (SELECT FROM pg_catalog.pg_user WHERE usename = 'eshop_app_user') THEN
      CREATE USER eshop_app_user WITH PASSWORD '${eshop.app.password}';
   END IF;
END
$$;

GRANT read_role, write_role TO eshop_app_user;
