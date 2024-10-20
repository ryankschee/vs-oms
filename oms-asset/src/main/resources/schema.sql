CREATE TABLE asset (
    id BIGINT PRIMARY KEY,
    product_code VARCHAR(255) NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    min_order_quantity INT NOT NULL
);