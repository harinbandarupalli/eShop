-- ============================================================
-- V0: Create eShop schema, extensions, roles, app user,
--     and the shared log_history() trigger function.
-- ============================================================

-- 1. Extensions
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- 2. Schema
CREATE SCHEMA IF NOT EXISTS eShop;

-- 3. Roles
DO $$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = 'eshop_read_role') THEN
        CREATE ROLE eshop_read_role;
    END IF;
    IF NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = 'eshop_write_role') THEN
        CREATE ROLE eshop_write_role;
    END IF;
END
$$;

-- 4. Permissions — read role
GRANT CONNECT ON DATABASE eshop TO eshop_read_role;
GRANT USAGE ON SCHEMA eShop TO eshop_read_role;
GRANT SELECT ON ALL TABLES IN SCHEMA eShop TO eshop_read_role;
ALTER DEFAULT PRIVILEGES IN SCHEMA eShop GRANT SELECT ON TABLES TO eshop_read_role;

-- 5. Permissions — write role (inherits read)
GRANT CONNECT ON DATABASE eshop TO eshop_write_role;
GRANT USAGE, CREATE ON SCHEMA eShop TO eshop_write_role;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA eShop TO eshop_write_role;
ALTER DEFAULT PRIVILEGES IN SCHEMA eShop GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO eshop_write_role;

-- 6. Application user
DO $$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_catalog.pg_user WHERE usename = 'eshop_app_user') THEN
        CREATE USER eshop_app_user WITH PASSWORD '${eshop.app.password}';
    END IF;
END
$$;

GRANT eshop_read_role, eshop_write_role TO eshop_app_user;

-- 7. Shared history trigger function
--    Automatically logs INSERT / UPDATE / DELETE to the
--    corresponding _history table (must mirror the parent table's columns).
CREATE OR REPLACE FUNCTION eShop.log_history()
RETURNS TRIGGER AS $$
DECLARE
    history_table TEXT;
    query         TEXT;
BEGIN
    history_table := TG_TABLE_NAME || '_history';

    IF (TG_OP = 'INSERT') THEN
        query := format(
            'INSERT INTO eShop.%I SELECT gen_random_uuid(), %L, NOW(), ($1).*',
            history_table, 'I'
        );
        EXECUTE query USING NEW;
        RETURN NEW;

    ELSIF (TG_OP = 'UPDATE') THEN
        query := format(
            'INSERT INTO eShop.%I SELECT gen_random_uuid(), %L, NOW(), ($1).*',
            history_table, 'U'
        );
        EXECUTE query USING NEW;
        RETURN NEW;

    ELSIF (TG_OP = 'DELETE') THEN
        query := format(
            'INSERT INTO eShop.%I SELECT gen_random_uuid(), %L, NOW(), ($1).*',
            history_table, 'D'
        );
        EXECUTE query USING OLD;
        RETURN OLD;
    END IF;

    RETURN NULL;
END;
$$ LANGUAGE plpgsql;
