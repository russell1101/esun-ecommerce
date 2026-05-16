USE esun_ecommerce;

-- 預設管理員帳號（密碼：test123）
INSERT INTO admin (username, password, status)
VALUES ('admin', '$2y$12$wSH2QUDgRD9xOEVM6AH.guUgHt96aLnkm160oE44VkVxxy6Rd2FIq', 1);

-- 範例商品資料（禮券類型）
INSERT INTO product (product_id, product_name, price, quantity, status, version)
VALUES
    ('G001', '7-11 伍佰元商品卡', 500.00, 100, 1, 0),
    ('G002', '全家 壹仟元禮物卡', 1000.00, 80, 1, 0),
    ('G003', '星巴克 隨行卡', 850.00, 120, 1, 0);

-- 範例會員資料（密碼：test123）
INSERT INTO member (username, password, email, phone, status)
VALUES ('member1', '$2y$12$19b5dFCokMMUbiiCaZP4AOP7cMvbqa2pJ1hdTjbmlLHd5Wq8i2Y1u', 'member1@example.com', '0912345678', 1);
