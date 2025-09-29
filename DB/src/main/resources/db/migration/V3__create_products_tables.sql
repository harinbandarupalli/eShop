-- V3: Create product_categories, products, and product_images tables

-- Product Categories
CREATE TABLE eShop.product_categories (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) UNIQUE NOT NULL,
    description TEXT,
    created_timestamp TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    last_updated_timestamp TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES eShop.users(id),
    last_updated_by UUID REFERENCES eShop.users(id)
);

-- Products
CREATE TABLE eShop.products (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    stock_quantity INT NOT NULL DEFAULT 0,
    category_id UUID REFERENCES eShop.product_categories(id),
    is_trending BOOLEAN DEFAULT FALSE,
    created_timestamp TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    last_updated_timestamp TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES eShop.users(id),
    last_updated_by UUID REFERENCES eShop.users(id)
);

-- Product Images
CREATE TABLE eShop.product_images (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_id UUID REFERENCES eShop.products(id) ON DELETE CASCADE,
    image_url TEXT NOT NULL,
    alt_text VARCHAR(255),
    created_timestamp TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    last_updated_timestamp TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES eShop.users(id),
    last_updated_by UUID REFERENCES eShop.users(id)
);

-- Product Categories History
CREATE TABLE eShop.product_categories_history (
    history_id UUID PRIMARY KEY,
    action CHAR(1) NOT NULL,
    changed_on TIMESTAMP WITH TIME ZONE NOT NULL,
    id UUID,
    name VARCHAR(100),
    description TEXT,
    created_timestamp TIMESTAMP WITH TIME ZONE,
    last_updated_timestamp TIMESTAMP WITH TIME ZONE,
    created_by UUID,
    last_updated_by UUID
);

CREATE TRIGGER product_categories_history_trigger
AFTER INSERT OR UPDATE OR DELETE ON eShop.product_categories
    FOR EACH ROW EXECUTE FUNCTION eShop.log_history();

-- Products History
CREATE TABLE eShop.products_history (
    history_id UUID PRIMARY KEY,
    action CHAR(1) NOT NULL,
    changed_on TIMESTAMP WITH TIME ZONE NOT NULL,
    id UUID,
    name VARCHAR(255),
    description TEXT,
    price DECIMAL(10, 2),
    stock_quantity INT,
    category_id UUID,
    is_trending BOOLEAN,
    created_timestamp TIMESTAMP WITH TIME ZONE,
    last_updated_timestamp TIMESTAMP WITH TIME ZONE,
    created_by UUID,
    last_updated_by UUID
);

CREATE TRIGGER products_history_trigger
AFTER INSERT OR UPDATE OR DELETE ON eShop.products
    FOR EACH ROW EXECUTE FUNCTION eShop.log_history();

-- Product Images History
CREATE TABLE eShop.product_images_history (
    history_id UUID PRIMARY KEY,
    action CHAR(1) NOT NULL,
    changed_on TIMESTAMP WITH TIME ZONE NOT NULL,
    id UUID,
    product_id UUID,
    image_url TEXT,
    alt_text VARCHAR(255),
    created_timestamp TIMESTAMP WITH TIME ZONE,
    last_updated_timestamp TIMESTAMP WITH TIME ZONE,
    created_by UUID,
    last_updated_by UUID
);

CREATE TRIGGER product_images_history_trigger
AFTER INSERT OR UPDATE OR DELETE ON eShop.product_images
    FOR EACH ROW EXECUTE FUNCTION eShop.log_history();
