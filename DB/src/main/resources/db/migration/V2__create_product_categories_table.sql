-- ============================================================
-- V2: Create product_categories table.
--     Categories are now linked to product_bags (not products)
--     via a many-to-many join table created in V4.
-- ============================================================

CREATE TABLE eShop.product_categories (
    id                     UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name                   VARCHAR(100) UNIQUE NOT NULL,
    description            TEXT,
    created_timestamp      TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    last_updated_timestamp TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by             VARCHAR(255) REFERENCES eShop.users(sub),
    last_updated_by        VARCHAR(255) REFERENCES eShop.users(sub)
);

-- History
CREATE TABLE eShop.product_categories_history (
    history_id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    action                 CHAR(1) NOT NULL,
    changed_on             TIMESTAMP WITH TIME ZONE NOT NULL,
    id                     UUID,
    name                   VARCHAR(100),
    description            TEXT,
    created_timestamp      TIMESTAMP WITH TIME ZONE,
    last_updated_timestamp TIMESTAMP WITH TIME ZONE,
    created_by             VARCHAR(255),
    last_updated_by        VARCHAR(255)
);

CREATE TRIGGER product_categories_history_trigger
AFTER INSERT OR UPDATE OR DELETE ON eShop.product_categories
    FOR EACH ROW EXECUTE FUNCTION eShop.log_history();
