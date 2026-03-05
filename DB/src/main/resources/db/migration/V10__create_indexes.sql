-- ============================================================
-- V10: Performance indexes.
--
--  Covers the main lookup patterns:
--    - Find carts/addresses/payments/orders by email
--    - Traverse product_bag_products and product_bag_categories
--    - Filter products by trending/active status
--    - Filter orders by status
-- ============================================================

-- Carts
CREATE INDEX idx_carts_email      ON eShop.carts(email);
CREATE INDEX idx_carts_session_id ON eShop.carts(session_id);
CREATE INDEX idx_carts_status     ON eShop.carts(status);

-- Cart Items
CREATE INDEX idx_cart_items_cart_id ON eShop.cart_items(cart_id);
CREATE INDEX idx_cart_items_bag_id  ON eShop.cart_items(bag_id);

-- Addresses
CREATE INDEX idx_addresses_email ON eShop.addresses(email);

-- Payment Methods
CREATE INDEX idx_payment_methods_email ON eShop.payment_methods(email);

-- Orders
CREATE INDEX idx_orders_email            ON eShop.orders(email);
CREATE INDEX idx_orders_status           ON eShop.orders(status);
CREATE INDEX idx_orders_cart_id          ON eShop.orders(cart_id);

-- Order Bag Snapshots
CREATE INDEX idx_order_bag_snapshots_order_id ON eShop.order_bag_snapshots(order_id);
CREATE INDEX idx_order_bag_snapshots_bag_id   ON eShop.order_bag_snapshots(bag_id);

-- Product Bag join tables
CREATE INDEX idx_product_bag_products_bag_id     ON eShop.product_bag_products(bag_id);
CREATE INDEX idx_product_bag_products_product_id ON eShop.product_bag_products(product_id);
CREATE INDEX idx_product_bag_categories_bag_id      ON eShop.product_bag_categories(bag_id);
CREATE INDEX idx_product_bag_categories_category_id ON eShop.product_bag_categories(category_id);

-- Products
CREATE INDEX idx_products_is_active   ON eShop.products(is_active);
CREATE INDEX idx_products_is_trending ON eShop.products(is_trending);

-- Product Images
CREATE INDEX idx_product_images_product_id ON eShop.product_images(product_id);

-- Product Bags
CREATE INDEX idx_product_bags_is_active ON eShop.product_bags(is_active);

-- Shipping Types
CREATE INDEX idx_shipping_types_is_active ON eShop.shipping_types(is_active);
