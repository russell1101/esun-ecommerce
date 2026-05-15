USE esun_ecommerce;

-- 預設管理員帳號（密碼：admin1234 → BCrypt hash）
INSERT INTO admin (username, password, status)
VALUES ('admin', '$2a$12$R9h/cIPz0gi.URNNX3kh2OYLS1mgU7H7BoGQSMQk9DUiCjRlz.Y8.', 1);

-- 範例商品資料（禮券類型）
INSERT INTO product (product_id, product_name, price, quantity, status, version)
VALUES
    ('G001', '7-11 伍佰元商品卡', 500.00, 100, 1, 0),
    ('G002', '全家 壹仟元禮物卡', 1000.00, 80, 1, 0),
    ('G003', '星巴克 隨行卡', 850.00, 120, 1, 0);

-- 範例會員資料（密碼：member1234 → BCrypt hash）
INSERT INTO member (username, password, email, phone, status)
VALUES ('member1', '$2a$12$IXGA7WcbqJNYnsoJ6Kc5FO3RkGX0kkO6/7kXvSrC9u8xd6l2k7HWq', 'member1@example.com', '0912345678', 1);
