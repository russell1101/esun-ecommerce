SET NAMES utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE DATABASE IF NOT EXISTS esun_ecommerce
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE esun_ecommerce;

-- admin 管理員表
CREATE TABLE IF NOT EXISTS admin (
    admin_id    INT            NOT NULL AUTO_INCREMENT COMMENT '管理員流水號',
    username    VARCHAR(50)    NOT NULL                COMMENT '帳號（唯一）',
    password    VARCHAR(255)   NOT NULL                COMMENT '密碼（BCrypt 加密）',
    status      TINYINT        NOT NULL DEFAULT 1      COMMENT '0:停用 / 1:啟用',
    inptime     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP              COMMENT '建立時間',
    updtime     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP
                                        ON UPDATE CURRENT_TIMESTAMP            COMMENT '更新時間',
    PRIMARY KEY (admin_id),
    UNIQUE KEY uq_admin_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='管理員資料表';

-- member 會員表
CREATE TABLE IF NOT EXISTS member (
    member_id   INT            NOT NULL AUTO_INCREMENT COMMENT '會員流水號',
    username    VARCHAR(50)    NOT NULL                COMMENT '帳號（唯一）',
    password    VARCHAR(255)   NOT NULL                COMMENT '密碼（BCrypt 加密）',
    email       VARCHAR(100)                           COMMENT '信箱',
    phone       VARCHAR(20)                            COMMENT '電話',
    status      TINYINT        NOT NULL DEFAULT 1      COMMENT '0:停用 / 1:啟用',
    inptime     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP              COMMENT '建立時間',
    updtime     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP
                                        ON UPDATE CURRENT_TIMESTAMP            COMMENT '更新時間',
    PRIMARY KEY (member_id),
    UNIQUE KEY uq_member_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='會員資料表';

-- product 商品表
CREATE TABLE IF NOT EXISTS product (
    product_sn   INT            NOT NULL AUTO_INCREMENT COMMENT '商品流水號',
    product_id   VARCHAR(10)    NOT NULL                COMMENT '商品編號',
    product_name VARCHAR(100)   NOT NULL                COMMENT '商品名稱',
    price        DECIMAL(10,2)  NOT NULL                COMMENT '售價',
    quantity     INT            NOT NULL DEFAULT 0      COMMENT '庫存',
    status       TINYINT        NOT NULL DEFAULT 1      COMMENT '0:下架 / 1:上架',
    version      INT            NOT NULL DEFAULT 0      COMMENT '樂觀鎖版本號',
    inptime      DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP              COMMENT '建立時間',
    updtime      DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP
                                         ON UPDATE CURRENT_TIMESTAMP            COMMENT '更新時間',
    PRIMARY KEY (product_sn),
    UNIQUE KEY uq_product_id (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品資料表';

-- orders 訂單表
CREATE TABLE IF NOT EXISTS orders (
    order_sn      INT           NOT NULL AUTO_INCREMENT COMMENT '訂單流水號',
    order_id      VARCHAR(20)   NOT NULL                COMMENT '訂單編號',
    member_id     INT           NOT NULL                COMMENT '會員流水號',
    total_price   DECIMAL(10,2) NOT NULL                COMMENT '訂單總金額',
    pay_status    TINYINT       NOT NULL DEFAULT 0      COMMENT '0:未付款 / 1:已付款',
    order_status  TINYINT       NOT NULL DEFAULT 1      COMMENT '1:建立 / 2:處理中 / 3:完成 / 9:取消',
    remark        VARCHAR(500)                          COMMENT '備註',
    inptime       DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP              COMMENT '建立時間',
    updtime       DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP
                                         ON UPDATE CURRENT_TIMESTAMP            COMMENT '更新時間',
    PRIMARY KEY (order_sn),
    UNIQUE KEY uq_order_id (order_id),
    CONSTRAINT fk_orders_member FOREIGN KEY (member_id) REFERENCES member (member_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='訂單資料表';

-- order_detail 訂單明細表
CREATE TABLE IF NOT EXISTS order_detail (
    order_item_sn INT           NOT NULL AUTO_INCREMENT COMMENT '明細流水號',
    order_sn      INT           NOT NULL                COMMENT 'FK → orders.order_sn',
    product_sn    INT           NOT NULL                COMMENT 'FK → product.product_sn',
    order_id      VARCHAR(20)   NOT NULL                COMMENT '訂單編號快照',
    product_id    VARCHAR(10)   NOT NULL                COMMENT '商品編號快照',
    quantity      INT           NOT NULL                COMMENT '購買數量',
    stand_price   DECIMAL(10,2) NOT NULL                COMMENT '單價快照（下單當下）',
    item_price    DECIMAL(10,2) NOT NULL                COMMENT '單品項總價',
    inptime       DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '建立時間',
    PRIMARY KEY (order_item_sn),
    CONSTRAINT fk_detail_order   FOREIGN KEY (order_sn)   REFERENCES orders  (order_sn),
    CONSTRAINT fk_detail_product FOREIGN KEY (product_sn) REFERENCES product (product_sn)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='訂單明細資料表';
