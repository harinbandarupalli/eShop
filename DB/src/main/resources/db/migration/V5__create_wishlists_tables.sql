-- V5: Create wishlists and wishlist_items tables

-- Wishlist (one per user)
CREATE TABLE eShop.wishlists (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         VARCHAR(255) UNIQUE NOT NULL REFERENCES eShop.users (sub) ON DELETE CASCADE,
    created_timestamp TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    last_updated_timestamp TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by      VARCHAR(255) REFERENCES eShop.users (sub),
    last_updated_by VARCHAR(255) REFERENCES eShop.users (sub)
);

-- Wishlist Items
CREATE TABLE eShop.wishlist_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    wishlist_id UUID NOT NULL REFERENCES eShop.wishlists(id) ON DELETE CASCADE,
    product_id UUID NOT NULL REFERENCES eShop.products(id) ON DELETE CASCADE,
    created_timestamp TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    last_updated_timestamp TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by      VARCHAR(255) REFERENCES eShop.users (sub),
    last_updated_by VARCHAR(255) REFERENCES eShop.users (sub),
    UNIQUE(wishlist_id, product_id)
);

-- Wishlists History
CREATE TABLE eShop.wishlists_history (
    history_id UUID PRIMARY KEY,
    action CHAR(1) NOT NULL,
    changed_on TIMESTAMP WITH TIME ZONE NOT NULL,
    id UUID,
    user_id         VARCHAR(255),
    created_timestamp TIMESTAMP WITH TIME ZONE,
    last_updated_timestamp TIMESTAMP WITH TIME ZONE,
    created_by      VARCHAR(255),
    last_updated_by VARCHAR(255)
);

CREATE TRIGGER wishlists_history_trigger
AFTER INSERT OR UPDATE OR DELETE ON eShop.wishlists
    FOR EACH ROW EXECUTE FUNCTION eShop.log_history();

-- Wishlist Items History
CREATE TABLE eShop.wishlist_items_history (
    history_id UUID PRIMARY KEY,
    action CHAR(1) NOT NULL,
    changed_on TIMESTAMP WITH TIME ZONE NOT NULL,
    id UUID,
    wishlist_id UUID,
    product_id UUID,
    created_timestamp TIMESTAMP WITH TIME ZONE,
    last_updated_timestamp TIMESTAMP WITH TIME ZONE,
    created_by      VARCHAR(255),
    last_updated_by VARCHAR(255)
);

CREATE TRIGGER wishlist_items_history_trigger
AFTER INSERT OR UPDATE OR DELETE ON eShop.wishlist_items
    FOR EACH ROW EXECUTE FUNCTION eShop.log_history();
