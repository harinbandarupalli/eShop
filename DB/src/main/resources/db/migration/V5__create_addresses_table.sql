-- ============================================================
-- V5: Create addresses table.
--
--  Linked by EMAIL (not user sub FK) so both authenticated
--  users and guest users can have addresses.
--  For authenticated users, email = users.email.
-- ============================================================

CREATE TABLE eShop.addresses (
    id                     UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email                  VARCHAR(255) NOT NULL,           -- identifies owner (guest or auth user)
    address_line1          VARCHAR(255) NOT NULL,
    address_line2          VARCHAR(255),
    city                   VARCHAR(100) NOT NULL,
    state_province_region  VARCHAR(100) NOT NULL,
    postal_code            VARCHAR(20)  NOT NULL,
    country                VARCHAR(100) NOT NULL,
    is_default             BOOLEAN NOT NULL DEFAULT FALSE,
    created_timestamp      TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    last_updated_timestamp TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by             VARCHAR(255) REFERENCES eShop.users(sub),  -- NULL for guest-created
    last_updated_by        VARCHAR(255) REFERENCES eShop.users(sub)
);

-- Index for fast lookup by email
CREATE INDEX idx_addresses_email ON eShop.addresses(email);

-- History
CREATE TABLE eShop.addresses_history (
    history_id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    action                 CHAR(1) NOT NULL,
    changed_on             TIMESTAMP WITH TIME ZONE NOT NULL,
    id                     UUID,
    email                  VARCHAR(255),
    address_line1          VARCHAR(255),
    address_line2          VARCHAR(255),
    city                   VARCHAR(100),
    state_province_region  VARCHAR(100),
    postal_code            VARCHAR(20),
    country                VARCHAR(100),
    is_default             BOOLEAN,
    created_timestamp      TIMESTAMP WITH TIME ZONE,
    last_updated_timestamp TIMESTAMP WITH TIME ZONE,
    created_by             VARCHAR(255),
    last_updated_by        VARCHAR(255)
);

CREATE TRIGGER addresses_history_trigger
AFTER INSERT OR UPDATE OR DELETE ON eShop.addresses
    FOR EACH ROW EXECUTE FUNCTION eShop.log_history();
