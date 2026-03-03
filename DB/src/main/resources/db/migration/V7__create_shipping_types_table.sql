-- V7: Create shipping_types table

CREATE TABLE eShop.shipping_types (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(50) UNIQUE NOT NULL, -- e.g., 'Standard', 'Express'
    cost DECIMAL(10, 2) NOT NULL,
    created_timestamp TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    last_updated_timestamp TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by      VARCHAR(255) REFERENCES eShop.users (sub),
    last_updated_by VARCHAR(255) REFERENCES eShop.users (sub)
);

-- Shipping Types History
CREATE TABLE eShop.shipping_types_history (
    history_id UUID PRIMARY KEY,
    action CHAR(1) NOT NULL,
    changed_on TIMESTAMP WITH TIME ZONE NOT NULL,
    id UUID,
    name VARCHAR(50),
    cost DECIMAL(10, 2),
    created_timestamp TIMESTAMP WITH TIME ZONE,
    last_updated_timestamp TIMESTAMP WITH TIME ZONE,
    created_by      VARCHAR(255),
    last_updated_by VARCHAR(255)
);

CREATE TRIGGER shipping_types_history_trigger
AFTER INSERT OR UPDATE OR DELETE ON eShop.shipping_types
    FOR EACH ROW EXECUTE FUNCTION eShop.log_history();
