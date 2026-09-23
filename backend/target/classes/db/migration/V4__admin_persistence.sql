CREATE TABLE admin_services (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    image_url VARCHAR(500),
    status VARCHAR(30) NOT NULL,
    icon VARCHAR(50),
    display_order INT NOT NULL DEFAULT 0
);

CREATE TABLE calculator_requests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    name VARCHAR(255),
    email VARCHAR(255),
    phone VARCHAR(50),
    property_type VARCHAR(50),
    monthly_bill DOUBLE,
    monthly_kwh DOUBLE,
    roof_area DOUBLE,
    recommended_kw DOUBLE,
    estimated_cost DOUBLE,
    annual_savings DOUBLE,
    payback_period_years DOUBLE,
    created_at TIMESTAMP NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE company_settings (
    id BIGINT PRIMARY KEY,
    company_name VARCHAR(255) NOT NULL,
    phone VARCHAR(50),
    email VARCHAR(255),
    address VARCHAR(1000)
);

INSERT INTO company_settings(id, company_name, phone, email, address)
VALUES (1, 'Solar Industries', '+91 9182432926', 'solarindustries80@gmail.com', 'Hyderabad, Telangana');

INSERT INTO admin_services(name, description, status, display_order) VALUES
('Solar Panels', 'Efficient solar panels for homes and businesses.', 'ACTIVE', 1),
('On-Grid Solar System', 'Grid-connected systems designed for everyday savings.', 'ACTIVE', 2),
('Off-Grid Solar System', 'Independent solar power for reliable energy access.', 'ACTIVE', 3),
('Hybrid Solar System', 'Flexible solar with grid and battery support.', 'ACTIVE', 4),
('Solar Water Pumps', 'Solar-powered pumping for homes and agriculture.', 'ACTIVE', 5),
('Solar Lights', 'Efficient outdoor and pathway lighting solutions.', 'ACTIVE', 6),
('Solar CCTV', 'Reliable surveillance powered by clean energy.', 'ACTIVE', 7),
('Solar Inverter', 'Inverters that keep your solar system dependable.', 'ACTIVE', 8),
('Solar Installation', 'Professional installation and commissioning.', 'ACTIVE', 9),
('Maintenance & Support', 'Ongoing service and maintenance for your system.', 'ACTIVE', 10);
