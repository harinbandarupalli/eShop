-- ============================================================
-- V8: Create carts and cart_items tables.
--
--  Cart identity:
--    - email column identifies both guest and auth users.
--    - For auth users, email matches users.email.
--    - Guest carts (not logged in) have email = the email
--      provided at checkout. When a guest converts, the
--      application merges carts with the same email.
--
--  Cart items reference product_bags (not individual products).
--  Users can: add a bag, edit quantity, delete a bag from cart.
--  Users CANNOT edit the bag contents.
-- ============================================================

-- Carts
CREATE TABLE eShop.carts (
    id                     UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email                  VARCHAR(255),                    -- NULL = anonymous session (pre-email)
    session_id             VARCHAR(255),                    -- browser session for anonymous carts
    status                 VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'
                               CHECK (status IN ('ACTIVE', 'CHECKED_OUT', 'ABANDONED', 'MERGED')),
    created_timestamp      TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    last_updated_timestamp TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by             VARCHAR(255),
    last_updated_by        VARCHAR(255),
    -- At least one of email or session_id must be present
    CONSTRAINT cart_identity_check CHECK (email IS NOT NULL OR session_id IS NOT NULL)
);

-- Cart Items — references product_bags
CREATE TABLE eShop.cart_items (
    id                     UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    cart_id                UUID NOT NULL REFERENCES eShop.carts(id) ON DELETE CASCADE,
    bag_id                 UUID NOT NULL REFERENCES eShop.product_bags(id) ON DELETE CASCADE,
    quantity               INT NOT NULL DEFAULT 1 CHECK (quantity > 0),
    created_timestamp      TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    last_updated_timestamp TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by             VARCHAR(255),
    last_updated_by        VARCHAR(255),
    UNIQUE (cart_id, bag_id)   -- one row per bag per cart; use ON CONFLICT to update qty
);

-- Carts History
CREATE TABLE eShop.carts_history (
    history_id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    action                 CHAR(1) NOT NULL,
    changed_on             TIMESTAMP WITH TIME ZONE NOT NULL,
    id                     UUID,
    email                  VARCHAR(255),
    session_id             VARCHAR(255),
    status                 VARCHAR(20),
    created_timestamp      TIMESTAMP WITH TIME ZONE,
    last_updated_timestamp TIMESTAMP WITH TIME ZONE,
    created_by             VARCHAR(255),
    last_updated_by        VARCHAR(255)
);

CREATE TRIGGER carts_history_trigger
AFTER INSERT OR UPDATE OR DELETE ON eShop.carts
    FOR EACH ROW EXECUTE FUNCTION eShop.log_history();

-- Cart Items History
CREATE TABLE eShop.cart_items_history (
    history_id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    action                 CHAR(1) NOT NULL,
    changed_on             TIMESTAMP WITH TIME ZONE NOT NULL,
    id                     UUID,
    cart_id                UUID,
    bag_id                 UUID,
    quantity               INT,
    created_timestamp      TIMESTAMP WITH TIME ZONE,
    last_updated_timestamp TIMESTAMP WITH TIME ZONE,
    created_by             VARCHAR(255),
    last_updated_by        VARCHAR(255)
);

CREATE TRIGGER cart_items_history_trigger
AFTER INSERT OR UPDATE OR DELETE ON eShop.cart_items
    FOR EACH ROW EXECUTE FUNCTION eShop.log_history();
