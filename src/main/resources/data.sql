-- Roles
INSERT INTO roles (name) VALUES 
    ('ROLE_ADMIN'),
    ('ROLE_USER')
ON DUPLICATE KEY UPDATE name=name;

-- Users (password: 'password')
INSERT INTO users (login, password, active) VALUES
    ('admin', '$2a$10$PH0pHhYxXQ3cw3yfpSS7IO0QZZHKzjNjMYK.zV7eoUe4IvEGnE5ym', true),
    ('user', '$2a$10$PH0pHhYxXQ3cw3yfpSS7IO0QZZHKzjNjMYK.zV7eoUe4IvEGnE5ym', true)
ON DUPLICATE KEY UPDATE login=login;

-- User Roles
INSERT INTO user_roles (user_id, role_id) VALUES
    (1, 1), -- admin has ROLE_ADMIN
    (1, 2), -- admin has ROLE_USER
    (2, 2)  -- user has ROLE_USER
ON DUPLICATE KEY UPDATE user_id=user_id;

-- Categories
INSERT INTO categories (name, description) VALUES
    ('Electronics', 'Electronic devices and accessories'),
    ('Clothing', 'Apparel and fashion items'),
    ('Books', 'Books and publications')
ON DUPLICATE KEY UPDATE name=name;

-- Products
INSERT INTO products (designation, price, quantity, category_id) VALUES
    ('Laptop', 999.99, 10, 1),
    ('Smartphone', 499.99, 20, 1),
    ('T-Shirt', 19.99, 100, 2),
    ('Jeans', 49.99, 50, 2),
    ('Java Programming', 29.99, 30, 3),
    ('Spring Boot Guide', 34.99, 25, 3)
ON DUPLICATE KEY UPDATE designation=designation;
