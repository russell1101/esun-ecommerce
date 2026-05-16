SET NAMES utf8mb4 COLLATE utf8mb4_unicode_ci;

USE esun_ecommerce;

DROP PROCEDURE IF EXISTS create_order;

DELIMITER $$

CREATE PROCEDURE create_order(
    IN  p_order_id    VARCHAR(20),
    IN  p_member_id   INT,
    IN  p_total_price DECIMAL(10,2),
    IN  p_items       JSON,
    IN  p_remark      VARCHAR(500)
)
BEGIN
    DECLARE v_i           INT DEFAULT 0;
    DECLARE v_len         INT;
    DECLARE v_order_sn    INT;
    DECLARE v_product_id  VARCHAR(10);
    DECLARE v_product_sn  INT;
    DECLARE v_quantity    INT;
    DECLARE v_stand_price DECIMAL(10,2);
    DECLARE v_item_price  DECIMAL(10,2);
    DECLARE v_stock       INT;

    -- SQL異常時ROLLBACK
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;

    START TRANSACTION;

    -- 建立訂單主檔
    INSERT INTO orders (order_id, member_id, total_price, pay_status, order_status, remark)
    VALUES (p_order_id, p_member_id, p_total_price, 0, 1, p_remark);

    SET v_order_sn = LAST_INSERT_ID();

    -- JSON_LENGTH 取得明細筆數，逐筆處理
    SET v_len = JSON_LENGTH(p_items);

    WHILE v_i < v_len DO

        SET v_product_id = JSON_UNQUOTE(
            JSON_EXTRACT(p_items, CONCAT('$[', v_i, '].productId'))
        );

        SET v_quantity = CAST(
            JSON_EXTRACT(p_items, CONCAT('$[', v_i, '].quantity'))
            AS UNSIGNED
        );

        SET v_stand_price = CAST(
            JSON_EXTRACT(p_items, CONCAT('$[', v_i, '].standPrice'))
            AS DECIMAL(10,2)
        );

        SET v_item_price = CAST(
            JSON_EXTRACT(p_items, CONCAT('$[', v_i, '].itemPrice'))
            AS DECIMAL(10,2)
        );

        -- FOR UPDATE 鎖定商品列
        SELECT quantity, product_sn INTO v_stock, v_product_sn
        FROM product
        WHERE product_id = v_product_id AND status = 1
        FOR UPDATE;

        IF v_stock IS NULL THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = '商品不存在或已下架';
        END IF;

        IF v_stock < v_quantity THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = '商品庫存不足';
        END IF;

        -- 新增訂單明細
        INSERT INTO order_detail (order_sn, product_sn, order_id, product_id, quantity, stand_price, item_price)
        VALUES (v_order_sn, v_product_sn, p_order_id, v_product_id, v_quantity, v_stand_price, v_item_price);

        -- 扣減庫存並同步遞增 version
        UPDATE product
        SET quantity = quantity - v_quantity,
            version  = version + 1
        WHERE product_sn = v_product_sn;

        SET v_i = v_i + 1;
    END WHILE;

    COMMIT;
END$$

DELIMITER ;
