USE esun_ecommerce;

DROP PROCEDURE IF EXISTS create_order;

DELIMITER $$

CREATE PROCEDURE create_order(
    IN  p_order_id    VARCHAR(20),
    IN  p_member_id   INT,
    IN  p_total_price DECIMAL(10,2),
    IN  p_items       JSON
)
BEGIN
    DECLARE v_i         INT DEFAULT 0;
    DECLARE v_len       INT;
    DECLARE v_product_id  VARCHAR(10);
    DECLARE v_quantity    INT;
    DECLARE v_stand_price DECIMAL(10,2);
    DECLARE v_item_price  DECIMAL(10,2);
    DECLARE v_stock       INT;

    -- ROLLBACK
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;

    START TRANSACTION;

    -- 1. 建立訂單主檔
    INSERT INTO orders (order_id, member_id, total_price, pay_status, order_status)
    VALUES (p_order_id, p_member_id, p_total_price, 0, 1);

    -- 2. 逐筆處理訂單明細
    SET v_len = JSON_LENGTH(p_items);

    WHILE v_i < v_len DO
        SET v_product_id  = JSON_UNQUOTE(JSON_EXTRACT(p_items, CONCAT('$[', v_i, '].productId')));
        SET v_quantity    = JSON_EXTRACT(p_items,           CONCAT('$[', v_i, '].quantity'));
        SET v_stand_price = JSON_EXTRACT(p_items,           CONCAT('$[', v_i, '].standPrice'));
        SET v_item_price  = JSON_EXTRACT(p_items,           CONCAT('$[', v_i, '].itemPrice'));

        -- 3. 確認庫存充足
        SELECT quantity INTO v_stock
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

        -- 4. 新增訂單明細
        INSERT INTO order_detail (order_id, product_id, quantity, stand_price, item_price)
        VALUES (p_order_id, v_product_id, v_quantity, v_stand_price, v_item_price);

        -- 5. 扣減庫存
        UPDATE product
        SET quantity = quantity - v_quantity,
            version  = version + 1
        WHERE product_id = v_product_id;

        SET v_i = v_i + 1;
    END WHILE;

    COMMIT;
END$$

DELIMITER ;
