-- Creating users table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(512) NOT NULL UNIQUE,
    registration_date TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Creating requests table
CREATE TABLE IF NOT EXISTS requests (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    description TEXT NOT NULL,
    requestor_id BIGINT NOT NULL,
    CONSTRAINT fk_requestor FOREIGN KEY (requestor_id) REFERENCES users (id)
);

-- Creating items table
CREATE TABLE IF NOT EXISTS items (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    available BOOLEAN DEFAULT TRUE,
    owner_id BIGINT NOT NULL,
    request_id BIGINT,
    CONSTRAINT fk_item_owner FOREIGN KEY (owner_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_item_request FOREIGN KEY (request_id) REFERENCES requests (id)
);

-- Creating bookings table
CREATE TABLE IF NOT EXISTS bookings (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    start_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    end_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    item_id BIGINT NOT NULL,
    booker_id BIGINT NOT NULL,
    status VARCHAR(20) DEFAULT 'WAITING',
    CONSTRAINT fk_booking_item FOREIGN KEY (item_id) REFERENCES items (id) ON DELETE CASCADE,
    CONSTRAINT fk_booking_booker FOREIGN KEY (booker_id) REFERENCES users (id) ON DELETE CASCADE
);

-- Creating comments table
CREATE TABLE IF NOT EXISTS comments (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    text TEXT NOT NULL,
    item_id BIGINT NOT NULL,
    author_id BIGINT NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_comment_item FOREIGN KEY (item_id) REFERENCES items (id) ON DELETE CASCADE,
    CONSTRAINT fk_comment_author FOREIGN KEY (author_id) REFERENCES users (id) ON DELETE CASCADE
);

-- Adding indices for performance optimization
CREATE INDEX IF NOT EXISTS idx_comments_item_id ON comments (item_id);
CREATE INDEX IF NOT EXISTS idx_bookings_item_id ON bookings (item_id);
CREATE INDEX IF NOT EXISTS idx_items_request_id ON items (request_id);
