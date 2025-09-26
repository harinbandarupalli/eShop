-- V8: Create payment_methods table

CREATE TABLE eShop.payment_methods (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES eShop.users(id) ON DELETE CASCADE,
    token VARCHAR(255) NOT NULL, -- Token from a payment provider like Stripe
    card_type VARCHAR(50),
    last_four_digits VARCHAR(4),
    billing_address_id UUID REFERENCES eShop.addresses(id),
    created_timestamp TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    last_updated_timestamp TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES eShop.users(id),
    last_updated_by UUID REFERENCES eShop.users(id)
);
