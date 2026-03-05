-- ============================================================
-- V4: Create product_bags and the two many-to-many join tables.
--
--  product_bags         — a curated bundle of products
--  product_bag_products — many-to-many: bag ↔ products
--                         (same product can be in multiple bags)
--                         (products cannot be removed from a bag
--                          by end users — enforced at app layer)
--  product_bag_categories — many-to-many: bag ↔ categories
--                           (same bag can belong to multiple categories)
--
--  Bag price: stored as an explicit display_price (admin-set).
--  If NULL, the application may derive it from SUM(product.price).
-- ============================================================

-- Product Bags
CREATE TABLE eShop.product_bags (
    id                     UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name                   VARCHAR(255) NOT NULL,
    description            TEXT,
    display_price          DECIMAL(10, 2) CHECK (display_price >= 0),  -- NULL = derived from products
    is_active              BOOLEAN NOT NULL DEFAULT TRUE,
    created_timestamp      TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    last_updated_timestamp TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by             VARCHAR(255) REFERENCES eShop.users(sub),
    last_updated_by        VARCHAR(255) REFERENCES eShop.users(sub)
);

-- Product Bag ↔ Products (many-to-many)
CREATE TABLE eShop.product_bag_products (
    bag_id                 UUID NOT NULL REFERENCES eShop.product_bags(id) ON DELETE CASCADE,
    product_id             UUID NOT NULL REFERENCES eShop.products(id) ON DELETE CASCADE,
    display_order          INT NOT NULL DEFAULT 0,
    created_timestamp      TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by             VARCHAR(255) REFERENCES eShop.users(sub),
    PRIMARY KEY (bag_id, product_id)
);

-- Product Bag ↔ Categories (many-to-many)
CREATE TABLE eShop.product_bag_categories (
    bag_id                 UUID NOT NULL REFERENCES eShop.product_bags(id) ON DELETE CASCADE,
    category_id            UUID NOT NULL REFERENCES eShop.product_categories(id) ON DELETE CASCADE,
    created_timestamp      TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by             VARCHAR(255) REFERENCES eShop.users(sub),
    PRIMARY KEY (bag_id, category_id)
);

-- Product Bags History
CREATE TABLE eShop.product_bags_history (
    history_id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    action                 CHAR(1) NOT NULL,
    changed_on             TIMESTAMP WITH TIME ZONE NOT NULL,
    id                     UUID,
    name                   VARCHAR(255),
    description            TEXT,
    display_price          DECIMAL(10, 2),
    is_active              BOOLEAN,
    created_timestamp      TIMESTAMP WITH TIME ZONE,
    last_updated_timestamp TIMESTAMP WITH TIME ZONE,
    created_by             VARCHAR(255),
    last_updated_by        VARCHAR(255)
);

CREATE TRIGGER product_bags_history_trigger
AFTER INSERT OR UPDATE OR DELETE ON eShop.product_bags
    FOR EACH ROW EXECUTE FUNCTION eShop.log_history();
