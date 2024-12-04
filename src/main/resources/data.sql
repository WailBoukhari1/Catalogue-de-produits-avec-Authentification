-- Initialize default roles
INSERT INTO roles (name) VALUES ('ROLE_USER') ON CONFLICT (name) DO NOTHING;
INSERT INTO roles (name) VALUES ('ROLE_ADMIN') ON CONFLICT (name) DO NOTHING;

-- Create default admin user (password: Admin123)
INSERT INTO users (login, password, email, active) 
VALUES ('admin', '$2a$12$K8Y6jQs0zK5U3o0MgZiHv.xqeM.Ft9xQOYq1KfKpxYr1D5Yp5kP6q', 'admin@localhost', true)
ON CONFLICT (login) DO NOTHING;

-- Assign ADMIN role to admin user
INSERT INTO user_roles (user_id, role)
SELECT u.id, 'ROLE_ADMIN' FROM users u
WHERE u.login = 'admin'
ON CONFLICT DO NOTHING;
