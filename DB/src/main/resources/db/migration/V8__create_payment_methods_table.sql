-- V8: Create payment_methods table

CREATE TABLE eShop.payment_methods (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         VARCHAR(255) NOT NULL REFERENCES eShop.users (sub) ON DELETE CASCADE,
    token VARCHAR(255), -- Token from a payment provider like Stripe
    cardholder_name VARCHAR(255) NOT NULL,
    last4 VARCHAR(4) NOT NULL,
    expiry_month INT NOT NULL,
    expiry_year INT NOT NULL,
    card_type VARCHAR(50),
    billing_address_id UUID REFERENCES eShop.addresses(id) ON DELETE SET NULL,
    billing_zipcode VARCHAR(20),
    created_timestamp TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    last_updated_timestamp TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by      VARCHAR(255) REFERENCES eShop.users (sub),
    last_updated_by VARCHAR(255) REFERENCES eShop.users (sub)
);

-- Payment Methods History
CREATE TABLE eShop.payment_methods_history (
    history_id UUID PRIMARY KEY,
    action CHAR(1) NOT NULL,
    changed_on TIMESTAMP WITH TIME ZONE NOT NULL,
    id UUID,
    user_id         VARCHAR(255),
    token VARCHAR(255),
    cardholder_name VARCHAR(255),
    last4 VARCHAR(4),
    expiry_month INT,
    expiry_year INT,
    card_type VARCHAR(50),
    billing_address_id UUID,
    billing_zipcode VARCHAR(20),
    created_timestamp TIMESTAMP WITH TIME ZONE,
    last_updated_timestamp TIMESTAMP WITH TIME ZONE,
    created_by      VARCHAR(255),
    last_updated_by VARCHAR(255)
);

CREATE TRIGGER payment_methods_history_trigger
AFTER INSERT OR UPDATE OR DELETE ON eShop.payment_methods
    FOR EACH ROW EXECUTE FUNCTION eShop.log_history();
