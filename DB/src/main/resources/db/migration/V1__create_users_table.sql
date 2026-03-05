-- ============================================================
-- V1: Create users table.
--     Authenticated users are identified by their OAuth `sub`.
--     username and email are both unique identifiers.
--     Guest users are NOT stored here — they are identified
--     solely by email on the relevant tables.
-- ============================================================

CREATE TABLE eShop.users (
    sub                    VARCHAR(255) PRIMARY KEY,
    username               VARCHAR(255) UNIQUE NOT NULL,   -- equals email for standard sign-up
    email                  VARCHAR(255) UNIQUE NOT NULL,
    first_name             VARCHAR(100),
    last_name              VARCHAR(100),
    created_timestamp      TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    last_updated_timestamp TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by             VARCHAR(255) REFERENCES eShop.users(sub),
    last_updated_by        VARCHAR(255) REFERENCES eShop.users(sub)
);

-- History
CREATE TABLE eShop.users_history (
    history_id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    action                 CHAR(1) NOT NULL,
    changed_on             TIMESTAMP WITH TIME ZONE NOT NULL,
    sub                    VARCHAR(255),
    username               VARCHAR(255),
    email                  VARCHAR(255),
    first_name             VARCHAR(100),
    last_name              VARCHAR(100),
    created_timestamp      TIMESTAMP WITH TIME ZONE,
    last_updated_timestamp TIMESTAMP WITH TIME ZONE,
    created_by             VARCHAR(255),
    last_updated_by        VARCHAR(255)
);

CREATE TRIGGER users_history_trigger
AFTER INSERT OR UPDATE OR DELETE ON eShop.users
    FOR EACH ROW EXECUTE FUNCTION eShop.log_history();
