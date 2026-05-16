SET NAMES utf8mb4 COLLATE utf8mb4_unicode_ci;

USE esun_ecommerce;

-- 預設管理員帳號（密碼：test123）
INSERT INTO admin (username, password, status)
VALUES ('admin', '$2a$12$wSH2QUDgRD9xOEVM6AH.guUgHt96aLnkm160oE44VkVxxy6Rd2FIq', 1);

-- 範例商品資料
INSERT INTO product (product_id, product_name, price, quantity, status, version)
VALUES
    ('P001', 'osii 舒壓按摩椅',   98000.00,  5, 1, 0),
    ('P002', '網友最愛起司蛋糕',    1200.00, 50, 1, 0),
    ('P003', '真愛密碼項鍊',        8500.00, 20, 1, 0);

-- 範例會員資料（密碼：test123）
INSERT INTO member (username, password, email, phone, status)
VALUES ('member1', '$2a$12$19b5dFCokMMUbiiCaZP4AOP7cMvbqa2pJ1hdTjbmlLHd5Wq8i2Y1u', 'member1@example.com', '0912345678', 1);
