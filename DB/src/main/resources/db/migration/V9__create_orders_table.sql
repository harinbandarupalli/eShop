-- ============================================================
-- V9: Create orders and order_bag_snapshots tables.
--
--  Order identity: email (works for both guests and auth users).
--  An order is created FROM a cart, capturing the exact state
--  at checkout via order_bag_snapshots.
--
--  Columns:
--    cart_id            — FK to the cart this order came from
--    address_id         — FK to the shipping address
--    payment_method_id  — FK to the payment method used
--    shipping_type_id   — FK to the shipping mode chosen
--
--  order_bag_snapshots: immutable price/quantity snapshot per bag
--    at the time of order placement. Ensures order history is
--    accurate even if product prices change later.
-- ============================================================

-- Orders
CREATE TABLE eShop.orders (
    id                     UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email                  VARCHAR(255) NOT NULL,
    cart_id                UUID NOT NULL REFERENCES eShop.carts(id),
    address_id             UUID NOT NULL REFERENCES eShop.addresses(id),
    payment_method_id      UUID NOT NULL REFERENCES eShop.payment_methods(id),
    shipping_type_id       UUID NOT NULL REFERENCES eShop.shipping_types(id),
    status                 VARCHAR(50) NOT NULL DEFAULT 'PENDING'
                               CHECK (status IN ('PENDING', 'PROCESSING', 'SHIPPED', 'DELIVERED', 'CANCELLED', 'REFUNDED')),
    total_amount           DECIMAL(10, 2) NOT NULL CHECK (total_amount >= 0),
    notes                  TEXT,                             -- optional buyer notes
    created_timestamp      TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    last_updated_timestamp TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by             VARCHAR(255) REFERENCES eShop.users(sub),
    last_updated_by        VARCHAR(255) REFERENCES eShop.users(sub)
);

-- Order Bag Snapshots — immutable record of bags & prices at checkout
CREATE TABLE eShop.order_bag_snapshots (
    id                     UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id               UUID NOT NULL REFERENCES eShop.orders(id) ON DELETE CASCADE,
    bag_id                 UUID NOT NULL REFERENCES eShop.product_bags(id),
    bag_name               VARCHAR(255) NOT NULL,            -- snapshot of name at time of order
    quantity               INT NOT NULL CHECK (quantity > 0),
    price_at_purchase      DECIMAL(10, 2) NOT NULL CHECK (price_at_purchase >= 0),
    created_timestamp      TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Orders History
CREATE TABLE eShop.orders_history (
    history_id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    action                 CHAR(1) NOT NULL,
    changed_on             TIMESTAMP WITH TIME ZONE NOT NULL,
    id                     UUID,
    email                  VARCHAR(255),
    cart_id                UUID,
    address_id             UUID,
    payment_method_id      UUID,
    shipping_type_id       UUID,
    status                 VARCHAR(50),
    total_amount           DECIMAL(10, 2),
    notes                  TEXT,
    created_timestamp      TIMESTAMP WITH TIME ZONE,
    last_updated_timestamp TIMESTAMP WITH TIME ZONE,
    created_by             VARCHAR(255),
    last_updated_by        VARCHAR(255)
);

CREATE TRIGGER orders_history_trigger
AFTER INSERT OR UPDATE OR DELETE ON eShop.orders
    FOR EACH ROW EXECUTE FUNCTION eShop.log_history();
