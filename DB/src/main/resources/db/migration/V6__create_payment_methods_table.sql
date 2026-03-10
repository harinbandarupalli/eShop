-- ============================================================
-- V6: Create payment_methods table.
--
--  Linked by EMAIL so both auth users and guests can store
--  payment info (e.g. during checkout).
--  Card data is tokenised — the raw card number is never stored.
-- ============================================================

CREATE TABLE eShop.payment_methods (
    id                     UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email                  VARCHAR(255) NOT NULL,           -- identifies owner (guest or auth user)
    token                  VARCHAR(255),                    -- provider token (Stripe, etc.)
    cardholder_name        VARCHAR(255) NOT NULL,
    last4                  VARCHAR(4)   NOT NULL,
    expiry_month           INT          NOT NULL CHECK (expiry_month BETWEEN 1 AND 12),
    expiry_year            INT          NOT NULL,
    card_type              VARCHAR(50),                     -- e.g. VISA, MASTERCARD
    billing_address_id     UUID REFERENCES eShop.addresses(id) ON DELETE SET NULL,
    is_default             BOOLEAN NOT NULL DEFAULT FALSE,
    created_timestamp      TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    last_updated_timestamp TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by             VARCHAR(255),
    last_updated_by        VARCHAR(255)
);

-- Index for fast lookup by email
CREATE INDEX idx_payment_methods_email ON eShop.payment_methods(email);

-- History
CREATE TABLE eShop.payment_methods_history (
    history_id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    action                 CHAR(1) NOT NULL,
    changed_on             TIMESTAMP WITH TIME ZONE NOT NULL,
    id                     UUID,
    email                  VARCHAR(255),
    token                  VARCHAR(255),
    cardholder_name        VARCHAR(255),
    last4                  VARCHAR(4),
    expiry_month           INT,
    expiry_year            INT,
    card_type              VARCHAR(50),
    billing_address_id     UUID,
    is_default             BOOLEAN,
    created_timestamp      TIMESTAMP WITH TIME ZONE,
    last_updated_timestamp TIMESTAMP WITH TIME ZONE,
    created_by             VARCHAR(255),
    last_updated_by        VARCHAR(255)
);

CREATE TRIGGER payment_methods_history_trigger
AFTER INSERT OR UPDATE OR DELETE ON eShop.payment_methods
    FOR EACH ROW EXECUTE FUNCTION eShop.log_history();
