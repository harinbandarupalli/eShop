-- V6: Create carts and cart_items tables

-- Shopping Cart
CREATE TABLE eShop.carts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID UNIQUE REFERENCES eShop.users(id) ON DELETE CASCADE, -- Can be NULL for guest carts
    created_timestamp TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    last_updated_timestamp TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES eShop.users(id),
    last_updated_by UUID REFERENCES eShop.users(id)
);

-- Cart Items
CREATE TABLE eShop.cart_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    cart_id UUID NOT NULL REFERENCES eShop.carts(id) ON DELETE CASCADE,
    product_id UUID NOT NULL REFERENCES eShop.products(id) ON DELETE CASCADE,
    quantity INT NOT NULL DEFAULT 1,
    created_timestamp TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    last_updated_timestamp TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES eShop.users(id),
    last_updated_by UUID REFERENCES eShop.users(id)
);

-- Carts History
CREATE TABLE eShop.carts_history (
    history_id UUID PRIMARY KEY,
    action CHAR(1) NOT NULL,
    changed_on TIMESTAMP WITH TIME ZONE NOT NULL,
    id UUID,
    user_id UUID,
    created_timestamp TIMESTAMP WITH TIME ZONE,
    last_updated_timestamp TIMESTAMP WITH TIME ZONE,
    created_by UUID,
    last_updated_by UUID
);

CREATE TRIGGER carts_history_trigger
AFTER INSERT OR UPDATE OR DELETE ON eShop.carts
    FOR EACH ROW EXECUTE FUNCTION eShop.log_history();

-- Cart Items History
CREATE TABLE eShop.cart_items_history (
    history_id UUID PRIMARY KEY,
    action CHAR(1) NOT NULL,
    changed_on TIMESTAMP WITH TIME ZONE NOT NULL,
    id UUID,
    cart_id UUID,
    product_id UUID,
    quantity INT,
    created_timestamp TIMESTAMP WITH TIME ZONE,
    last_updated_timestamp TIMESTAMP WITH TIME ZONE,
    created_by UUID,
    last_updated_by UUID
);

CREATE TRIGGER cart_items_history_trigger
AFTER INSERT OR UPDATE OR DELETE ON eShop.cart_items
    FOR EACH ROW EXECUTE FUNCTION eShop.log_history();
