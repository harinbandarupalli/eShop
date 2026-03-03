-- V2: Create users table

CREATE TABLE eShop.users (
    sub VARCHAR(255) PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    created_timestamp TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    last_updated_timestamp TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255) REFERENCES eShop.users(sub), -- Nullable for the very first user
    last_updated_by VARCHAR(255) REFERENCES eShop.users(sub)
);

-- Users History
CREATE TABLE eShop.users_history (
    history_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    action CHAR(1) NOT NULL,
    changed_on TIMESTAMP WITH TIME ZONE NOT NULL,
    sub VARCHAR(255),
    username VARCHAR(50),
    email VARCHAR(255),
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    created_timestamp TIMESTAMP WITH TIME ZONE,
    last_updated_timestamp TIMESTAMP WITH TIME ZONE,
    created_by VARCHAR(255),
    last_updated_by VARCHAR(255)
);

CREATE TRIGGER users_history_trigger
AFTER INSERT OR UPDATE OR DELETE ON eShop.users
    FOR EACH ROW EXECUTE FUNCTION eShop.log_history();
