-- =====================================================
-- Food & Drink Formulation Management System - Database Schema
-- =====================================================

-- Create database
CREATE DATABASE IF NOT EXISTS formulation_system;
USE formulation_system;

-- =====================================================
-- PERSONS TABLES
-- =====================================================

-- Admins table
CREATE TABLE admins (
    admin_id INT PRIMARY KEY,
    person_id INT,
    name VARCHAR(255) NOT NULL,
    address VARCHAR(500),
    contact VARCHAR(50),
    date_of_birth DATE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) DEFAULT 'ADMIN',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_admin_name (name)
);

-- Authors table
CREATE TABLE authors (
    author_id INT PRIMARY KEY,
    person_id INT,
    name VARCHAR(255) NOT NULL,
    address VARCHAR(500),
    contact VARCHAR(50),
    date_of_birth DATE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) DEFAULT 'AUTHOR',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_author_name (name)
);

-- Customers table
CREATE TABLE customers (
    customer_id INT PRIMARY KEY,
    person_id INT,
    name VARCHAR(255) NOT NULL,
    address VARCHAR(500),
    contact VARCHAR(50),
    date_of_birth DATE,
    age INT,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) DEFAULT 'CUSTOMER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_customer_name (name)
);

-- Consumer specific info
CREATE TABLE consumer_specific_info (
    info_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT,
    profile TEXT,
    age_range VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE
);

-- Consumer allergies
CREATE TABLE consumer_allergies (
    allergy_id INT AUTO_INCREMENT PRIMARY KEY,
    info_id INT,
    allergy VARCHAR(255),
    FOREIGN KEY (info_id) REFERENCES consumer_specific_info(info_id) ON DELETE CASCADE
);

-- =====================================================
-- ITEMS (FORMULATIONS) TABLES
-- =====================================================

-- Items (base table for Food and Drink)
CREATE TABLE items (
    item_id INT PRIMARY KEY,
    item_type ENUM('FOOD', 'DRINK') NOT NULL,
    name VARCHAR(255) NOT NULL,
    price DECIMAL(10, 2),
    entry_date DATE,
    expiry_date DATE,
    average_price_per_kg DECIMAL(10, 2),
    author_id INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (author_id) REFERENCES authors(author_id) ON DELETE SET NULL,
    INDEX idx_item_name (name),
    INDEX idx_item_type (item_type)
);

-- Food specific data
CREATE TABLE foods (
    food_id INT PRIMARY KEY,
    item_id INT UNIQUE,
    FOREIGN KEY (item_id) REFERENCES items(item_id) ON DELETE CASCADE,
    FOREIGN KEY (food_id) REFERENCES items(item_id) ON DELETE CASCADE
);

-- Drink specific data
CREATE TABLE drinks (
    drink_id INT PRIMARY KEY,
    item_id INT UNIQUE,
    FOREIGN KEY (item_id) REFERENCES items(item_id) ON DELETE CASCADE,
    FOREIGN KEY (drink_id) REFERENCES items(item_id) ON DELETE CASCADE
);

-- =====================================================
-- INGREDIENTS
-- =====================================================

CREATE TABLE ingredients (
    ingredient_id INT AUTO_INCREMENT PRIMARY KEY,
    item_id INT,
    name VARCHAR(255) NOT NULL,
    weight DECIMAL(10, 2),
    volume DECIMAL(10, 2),
    fraction DECIMAL(10, 4),
    unit VARCHAR(50),
    FOREIGN KEY (item_id) REFERENCES items(item_id) ON DELETE CASCADE,
    INDEX idx_ingredient_name (name)
);

-- =====================================================
-- CONDITIONS
-- =====================================================

-- Lab conditions (Optimal conditions)
CREATE TABLE lab_conditions (
    condition_id INT AUTO_INCREMENT PRIMARY KEY,
    item_id INT,
    temperature DECIMAL(10, 2),
    pressure DECIMAL(10, 2),
    moisture DECIMAL(10, 2),
    vibration DECIMAL(10, 2),
    period INT,
    FOREIGN KEY (item_id) REFERENCES items(item_id) ON DELETE CASCADE
);

-- Conservation conditions
CREATE TABLE conservation_conditions (
    condition_id INT AUTO_INCREMENT PRIMARY KEY,
    item_id INT,
    temperature DECIMAL(10, 2),
    moisture DECIMAL(10, 2),
    container VARCHAR(255),
    FOREIGN KEY (item_id) REFERENCES items(item_id) ON DELETE CASCADE
);

-- Consumption conditions
CREATE TABLE consumption_conditions (
    condition_id INT AUTO_INCREMENT PRIMARY KEY,
    item_id INT,
    temperature DECIMAL(10, 2),
    moisture DECIMAL(10, 2),
    FOREIGN KEY (item_id) REFERENCES items(item_id) ON DELETE CASCADE
);

-- =====================================================
-- PREPARATION PROTOCOL
-- =====================================================

CREATE TABLE preparation_protocols (
    protocol_id INT AUTO_INCREMENT PRIMARY KEY,
    item_id INT,
    step_number INT,
    step_description TEXT,
    step_temp DECIMAL(10, 2),
    step_pressure DECIMAL(10, 2),
    step_moisture DECIMAL(10, 2),
    step_vibration DECIMAL(10, 2),
    step_period INT,
    FOREIGN KEY (item_id) REFERENCES items(item_id) ON DELETE CASCADE,
    INDEX idx_protocol_item (item_id, step_number)
);

-- =====================================================
-- STANDARDS
-- =====================================================

CREATE TABLE standards (
    standard_id INT AUTO_INCREMENT PRIMARY KEY,
    item_id INT,
    standard_text VARCHAR(500),
    FOREIGN KEY (item_id) REFERENCES items(item_id) ON DELETE CASCADE
);

-- =====================================================
-- RESTRICTIONS
-- =====================================================

-- Trademark information
CREATE TABLE trademark_info (
    trademark_id INT AUTO_INCREMENT PRIMARY KEY,
    item_id INT,
    date VARCHAR(50),
    authority VARCHAR(255),
    authorization_number INT,
    issue_date DATE,
    FOREIGN KEY (item_id) REFERENCES items(item_id) ON DELETE CASCADE
);

-- Veto information
CREATE TABLE vetos (
    veto_id INT AUTO_INCREMENT PRIMARY KEY,
    item_id INT,
    is_vetoed BOOLEAN DEFAULT FALSE,
    reason TEXT,
    veto_date DATE,
    initiator_id INT,
    initiator_type ENUM('ADMIN', 'AUTHOR', 'CUSTOMER'),
    FOREIGN KEY (item_id) REFERENCES items(item_id) ON DELETE CASCADE,
    INDEX idx_veto_status (is_vetoed)
);

-- =====================================================
-- FEEDBACK
-- =====================================================

CREATE TABLE feedbacks (
    feedback_id INT AUTO_INCREMENT PRIMARY KEY,
    item_id INT,
    customer_id INT,
    customer_name VARCHAR(255),
    comment TEXT,
    is_like BOOLEAN,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (item_id) REFERENCES items(item_id) ON DELETE CASCADE,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE SET NULL,
    INDEX idx_feedback_item (item_id),
    INDEX idx_feedback_date (timestamp)
);

-- =====================================================
-- PURCHASES
-- =====================================================

CREATE TABLE purchases (
    purchase_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT,
    item_id INT,
    item_name VARCHAR(255),
    price DECIMAL(10, 2),
    purchase_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    payment_method VARCHAR(50),
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE,
    FOREIGN KEY (item_id) REFERENCES items(item_id) ON DELETE SET NULL,
    INDEX idx_purchase_customer (customer_id),
    INDEX idx_purchase_date (purchase_date)
);

-- =====================================================
-- FAVORITES
-- =====================================================

CREATE TABLE favorites (
    favorite_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT,
    item_id INT,
    added_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE,
    FOREIGN KEY (item_id) REFERENCES items(item_id) ON DELETE CASCADE,
    UNIQUE KEY unique_favorite (customer_id, item_id)
);

-- =====================================================
-- SIDE EFFECTS
-- =====================================================

CREATE TABLE side_effects (
    side_effect_id INT AUTO_INCREMENT PRIMARY KEY,
    item_id INT,
    exposed_profile TEXT,
    FOREIGN KEY (item_id) REFERENCES items(item_id) ON DELETE CASCADE
);

CREATE TABLE side_effect_symptoms (
    symptom_id INT AUTO_INCREMENT PRIMARY KEY,
    side_effect_id INT,
    symptom TEXT,
    FOREIGN KEY (side_effect_id) REFERENCES side_effects(side_effect_id) ON DELETE CASCADE
);

CREATE TABLE side_effect_remedies (
    remedy_id INT AUTO_INCREMENT PRIMARY KEY,
    side_effect_id INT,
    remedy TEXT,
    FOREIGN KEY (side_effect_id) REFERENCES side_effects(side_effect_id) ON DELETE CASCADE
);

-- =====================================================
-- AUDIT TRAIL
-- =====================================================

CREATE TABLE audit_trail (
    audit_id INT AUTO_INCREMENT PRIMARY KEY,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    user_type VARCHAR(50),
    user_name VARCHAR(255),
    action TEXT,
    INDEX idx_audit_timestamp (timestamp),
    INDEX idx_audit_user (user_type, user_name)
);

-- =====================================================
-- NOTIFICATIONS
-- =====================================================

CREATE TABLE notifications (
    notification_id INT AUTO_INCREMENT PRIMARY KEY,
    sender_admin_id INT,
    sender_admin_name VARCHAR(255),
    recipient_author_id INT,
    recipient_author_name VARCHAR(255),
    item_id INT,
    item_name VARCHAR(255),
    issue_description TEXT,
    severity ENUM('LOW', 'MEDIUM', 'HIGH', 'CRITICAL'),
    issue_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status ENUM('PENDING', 'RESOLVED', 'IGNORED') DEFAULT 'PENDING',
    resolution_date TIMESTAMP NULL,
    resolution_notes TEXT,
    FOREIGN KEY (sender_admin_id) REFERENCES admins(admin_id) ON DELETE SET NULL,
    FOREIGN KEY (recipient_author_id) REFERENCES authors(author_id) ON DELETE CASCADE,
    FOREIGN KEY (item_id) REFERENCES items(item_id) ON DELETE CASCADE,
    INDEX idx_notification_recipient (recipient_author_id, status)
);

-- =====================================================
-- ITEM AUTHORS (Many-to-Many relationship)
-- =====================================================

CREATE TABLE item_authors (
    item_id INT,
    author_id INT,
    PRIMARY KEY (item_id, author_id),
    FOREIGN KEY (item_id) REFERENCES items(item_id) ON DELETE CASCADE,
    FOREIGN KEY (author_id) REFERENCES authors(author_id) ON DELETE CASCADE
);

-- =====================================================
-- INSERT DEFAULT ADMIN
-- =====================================================

INSERT INTO admins (admin_id, name, address, contact, date_of_birth, password, role)
VALUES (1, 'System Admin', 'HQ', '+1-000-0000', '1980-01-01', 'admin123', 'ADMIN')
ON DUPLICATE KEY UPDATE name = name;

-- =====================================================
-- VIEWS FOR EASIER QUERIES
-- =====================================================

-- View: Complete Item Information
CREATE VIEW v_items_complete AS
SELECT
    i.item_id,
    i.item_type,
    i.name,
    i.price,
    i.entry_date,
    i.expiry_date,
    i.average_price_per_kg,
    i.author_id,
    a.name AS author_name,
    COALESCE(v.is_vetoed, FALSE) AS is_vetoed,
    v.reason AS veto_reason,
    COUNT(DISTINCT ing.ingredient_id) AS ingredient_count,
    COUNT(DISTINCT f.feedback_id) AS feedback_count,
    COUNT(DISTINCT p.purchase_id) AS purchase_count
FROM items i
LEFT JOIN authors a ON i.author_id = a.author_id
LEFT JOIN vetos v ON i.item_id = v.item_id
LEFT JOIN ingredients ing ON i.item_id = ing.item_id
LEFT JOIN feedbacks f ON i.item_id = f.item_id
LEFT JOIN purchases p ON i.item_id = p.item_id
GROUP BY i.item_id, i.item_type, i.name, i.price, i.entry_date,
         i.expiry_date, i.average_price_per_kg, i.author_id,
         a.name, v.is_vetoed, v.reason;

-- View: Customer Purchase History
CREATE VIEW v_customer_purchases AS
SELECT
    c.customer_id,
    c.name AS customer_name,
    p.purchase_id,
    p.item_id,
    p.item_name,
    p.price,
    p.purchase_date,
    p.payment_method
FROM customers c
JOIN purchases p ON c.customer_id = p.customer_id
ORDER BY p.purchase_date DESC;

-- View: Author Formulation Summary
CREATE VIEW v_author_formulations AS
SELECT
    a.author_id,
    a.name AS author_name,
    COUNT(i.item_id) AS total_formulations,
    SUM(CASE WHEN i.item_type = 'FOOD' THEN 1 ELSE 0 END) AS food_count,
    SUM(CASE WHEN i.item_type = 'DRINK' THEN 1 ELSE 0 END) AS drink_count,
    AVG(i.price) AS avg_price
FROM authors a
LEFT JOIN items i ON a.author_id = i.author_id
GROUP BY a.author_id, a.name;

-- =====================================================
-- INDEXES FOR PERFORMANCE
-- =====================================================

CREATE INDEX idx_items_author ON items(author_id);
CREATE INDEX idx_feedbacks_customer ON feedbacks(customer_id);
CREATE INDEX idx_purchases_item ON purchases(item_id);
CREATE INDEX idx_notifications_status ON notifications(status);

-- =====================================================
-- END OF SCHEMA
-- =====================================================