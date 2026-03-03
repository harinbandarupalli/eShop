-- V4: Create addresses table

CREATE TABLE eShop.addresses (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         VARCHAR(255) NOT NULL REFERENCES eShop.users (sub) ON DELETE CASCADE,
    address_line1 VARCHAR(255) NOT NULL,
    address_line2 VARCHAR(255),
    city VARCHAR(100) NOT NULL,
    state_province_region VARCHAR(100) NOT NULL,
    postal_code VARCHAR(20) NOT NULL,
    country VARCHAR(100) NOT NULL,
    created_timestamp TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    last_updated_timestamp TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by      VARCHAR(255) REFERENCES eShop.users (sub),
    last_updated_by VARCHAR(255) REFERENCES eShop.users (sub)
);

-- Addresses History
CREATE TABLE eShop.addresses_history (
    history_id UUID PRIMARY KEY,
    action CHAR(1) NOT NULL,
    changed_on TIMESTAMP WITH TIME ZONE NOT NULL,
    id UUID,
    user_id         VARCHAR(255),
    address_line1 VARCHAR(255),
    address_line2 VARCHAR(255),
    city VARCHAR(100),
    state_province_region VARCHAR(100),
    postal_code VARCHAR(20),
    country VARCHAR(100),
    created_timestamp TIMESTAMP WITH TIME ZONE,
    last_updated_timestamp TIMESTAMP WITH TIME ZONE,
    created_by      VARCHAR(255),
    last_updated_by VARCHAR(255)
);

CREATE TRIGGER addresses_history_trigger
AFTER INSERT OR UPDATE OR DELETE ON eShop.addresses
    FOR EACH ROW EXECUTE FUNCTION eShop.log_history();
