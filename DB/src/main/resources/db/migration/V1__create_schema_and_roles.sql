-- V1: Create eShop schema, roles, and user

CREATE
EXTENSION IF NOT EXISTS "pgcrypto";

-- 1. Schema Creation
CREATE SCHEMA IF NOT EXISTS eShop;

-- 2. Roles and Permissions
DO
$$
BEGIN
   IF
NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = 'read_role') THEN
CREATE ROLE read_role;
END IF;
   IF
NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = 'write_role') THEN
CREATE ROLE write_role;
END IF;
END
$$;

GRANT CONNECT
ON DATABASE eshop TO read_role;
GRANT USAGE ON SCHEMA
eShop TO read_role;
GRANT
SELECT
ON ALL TABLES IN SCHEMA eShop TO read_role;
ALTER
DEFAULT PRIVILEGES IN SCHEMA eShop GRANT
SELECT
ON TABLES TO read_role;

GRANT
CONNECT
ON DATABASE eshop TO write_role;
GRANT USAGE, CREATE
ON SCHEMA eShop TO write_role;
GRANT
SELECT,
INSERT
,
UPDATE,
DELETE
ON ALL TABLES IN SCHEMA eShop TO write_role;
ALTER
DEFAULT PRIVILEGES IN SCHEMA eShop GRANT
SELECT,
INSERT
,
UPDATE,
DELETE
ON TABLES TO write_role;

-- 3. Application User
DO
$$
BEGIN
   IF
NOT EXISTS (SELECT FROM pg_catalog.pg_user WHERE usename = 'eshop_app_user') THEN
      CREATE
USER eshop_app_user WITH PASSWORD '${eshop.app.password}';
END IF;
END
$$;

GRANT read_role, write_role TO eshop_app_user;


CREATE
OR REPLACE FUNCTION eShop.log_history()
RETURNS TRIGGER AS $$
DECLARE
history_table_name TEXT;
  query
TEXT;
BEGIN
  history_table_name
:= TG_TABLE_NAME || '_history';

  IF
(TG_OP = 'INSERT') THEN
    query := format(
      'INSERT INTO eShop.%I SELECT gen_random_uuid(), %L, NOW(), ($1).*',
      history_table_name,
      'I'
    );
EXECUTE query USING NEW;
RETURN NEW;

ELSIF
(TG_OP = 'UPDATE') THEN
    query := format(
      'INSERT INTO eShop.%I SELECT gen_random_uuid(), %L, NOW(), ($1).*',
      history_table_name,
      'U'
    );
EXECUTE query USING NEW;
RETURN NEW;

ELSIF
(TG_OP = 'DELETE') THEN
    query := format(
      'INSERT INTO eShop.%I SELECT gen_random_uuid(), %L, NOW(), ($1).*',
      history_table_name,
      'D'
    );
EXECUTE query USING OLD;
RETURN OLD;
END IF;

RETURN NULL;
END;
$$
LANGUAGE plpgsql;
