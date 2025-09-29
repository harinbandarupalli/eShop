-- V9: Create orders and order_items tables

-- Orders
CREATE TABLE eShop.orders (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES eShop.users(id),
    total_amount DECIMAL(10, 2) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING', -- e.g., PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELED
    shipping_address_id UUID NOT NULL REFERENCES eShop.addresses(id),
    billing_address_id UUID NOT NULL REFERENCES eShop.addresses(id),
    shipping_type_id UUID NOT NULL REFERENCES eShop.shipping_types(id),
    payment_method_id UUID REFERENCES eShop.payment_methods(id),
    created_timestamp TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    last_updated_timestamp TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES eShop.users(id),
    last_updated_by UUID REFERENCES eShop.users(id)
);

-- Order Items
CREATE TABLE eShop.order_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id UUID NOT NULL REFERENCES eShop.orders(id) ON DELETE CASCADE,
    product_id UUID NOT NULL REFERENCES eShop.products(id),
    quantity INT NOT NULL,
    price_at_purchase DECIMAL(10, 2) NOT NULL,
    created_timestamp TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    last_updated_timestamp TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES eShop.users(id),
    last_updated_by UUID REFERENCES eShop.users(id)
);

-- Orders History
CREATE TABLE eShop.orders_history (
    history_id UUID PRIMARY KEY,
    action CHAR(1) NOT NULL,
    changed_on TIMESTAMP WITH TIME ZONE NOT NULL,
    id UUID,
    user_id UUID,
    total_amount DECIMAL(10, 2),
    status VARCHAR(50),
    shipping_address_id UUID,
    billing_address_id UUID,
    shipping_type_id UUID,
    payment_method_id UUID,
    created_timestamp TIMESTAMP WITH TIME ZONE,
    last_updated_timestamp TIMESTAMP WITH TIME ZONE,
    created_by UUID,
    last_updated_by UUID
);

CREATE TRIGGER orders_history_trigger
AFTER INSERT OR UPDATE OR DELETE ON eShop.orders
    FOR EACH ROW EXECUTE FUNCTION eShop.log_history();

-- Order Items History
CREATE TABLE eShop.order_items_history (
    history_id UUID PRIMARY KEY,
    action CHAR(1) NOT NULL,
    changed_on TIMESTAMP WITH TIME ZONE NOT NULL,
    id UUID,
    order_id UUID,
    product_id UUID,
    quantity INT,
    price_at_purchase DECIMAL(10, 2),
    created_timestamp TIMESTAMP WITH TIME ZONE,
    last_updated_timestamp TIMESTAMP WITH TIME ZONE,
    created_by UUID,
    last_updated_by UUID
);

CREATE TRIGGER order_items_history_trigger
AFTER INSERT OR UPDATE OR DELETE ON eShop.order_items
    FOR EACH ROW EXECUTE FUNCTION eShop.log_history();
