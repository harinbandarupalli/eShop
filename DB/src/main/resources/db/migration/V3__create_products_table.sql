-- ============================================================
-- V3: Create products table and product_images table.
--     NOTE: products no longer carry a category_id.
--     Categorisation is done at the product_bag level (V4).
-- ============================================================

-- Products
CREATE TABLE eShop.products (
    id                     UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name                   VARCHAR(255) NOT NULL,
    description            TEXT,
    price                  DECIMAL(10, 2) NOT NULL CHECK (price >= 0),
    stock_quantity         INT NOT NULL DEFAULT 0 CHECK (stock_quantity >= 0),
    is_active              BOOLEAN NOT NULL DEFAULT TRUE,
    is_trending            BOOLEAN NOT NULL DEFAULT FALSE,
    created_timestamp      TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    last_updated_timestamp TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by             VARCHAR(255) REFERENCES eShop.users(sub),
    last_updated_by        VARCHAR(255) REFERENCES eShop.users(sub)
);

-- Product Images
CREATE TABLE eShop.product_images (
    id                     UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_id             UUID NOT NULL REFERENCES eShop.products(id) ON DELETE CASCADE,
    image_url              TEXT NOT NULL,
    alt_text               VARCHAR(255),
    display_order          INT NOT NULL DEFAULT 0,
    created_timestamp      TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    last_updated_timestamp TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by             VARCHAR(255) REFERENCES eShop.users(sub),
    last_updated_by        VARCHAR(255) REFERENCES eShop.users(sub)
);

-- Products History
CREATE TABLE eShop.products_history (
    history_id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    action                 CHAR(1) NOT NULL,
    changed_on             TIMESTAMP WITH TIME ZONE NOT NULL,
    id                     UUID,
    name                   VARCHAR(255),
    description            TEXT,
    price                  DECIMAL(10, 2),
    stock_quantity         INT,
    is_active              BOOLEAN,
    is_trending            BOOLEAN,
    created_timestamp      TIMESTAMP WITH TIME ZONE,
    last_updated_timestamp TIMESTAMP WITH TIME ZONE,
    created_by             VARCHAR(255),
    last_updated_by        VARCHAR(255)
);

CREATE TRIGGER products_history_trigger
AFTER INSERT OR UPDATE OR DELETE ON eShop.products
    FOR EACH ROW EXECUTE FUNCTION eShop.log_history();

-- Product Images History
CREATE TABLE eShop.product_images_history (
    history_id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    action                 CHAR(1) NOT NULL,
    changed_on             TIMESTAMP WITH TIME ZONE NOT NULL,
    id                     UUID,
    product_id             UUID,
    image_url              TEXT,
    alt_text               VARCHAR(255),
    display_order          INT,
    created_timestamp      TIMESTAMP WITH TIME ZONE,
    last_updated_timestamp TIMESTAMP WITH TIME ZONE,
    created_by             VARCHAR(255),
    last_updated_by        VARCHAR(255)
);

CREATE TRIGGER product_images_history_trigger
AFTER INSERT OR UPDATE OR DELETE ON eShop.product_images
    FOR EACH ROW EXECUTE FUNCTION eShop.log_history();
