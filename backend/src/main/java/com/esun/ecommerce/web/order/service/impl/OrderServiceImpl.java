package com.esun.ecommerce.web.order.service.impl;

import com.esun.ecommerce.core.annotation.DbOperation;
import com.esun.ecommerce.core.entity.Member;
import com.esun.ecommerce.core.entity.Order;
import com.esun.ecommerce.core.entity.OrderDetail;
import com.esun.ecommerce.core.entity.Product;
import com.esun.ecommerce.core.exception.BusinessException;
import com.esun.ecommerce.web.auth.repository.MemberRepository;
import com.esun.ecommerce.web.order.dto.*;
import com.esun.ecommerce.web.order.repository.OrderDetailRepository;
import com.esun.ecommerce.web.order.repository.OrderRepository;
import com.esun.ecommerce.web.order.service.OrderService;
import com.esun.ecommerce.web.product.repository.ProductRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.CallableStatement;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;
    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    /**
     * 建立訂單
     * @Version：樂觀鎖，防止 admin 並發修改商品資料互蓋
     */
    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @DbOperation(action = "建立訂單")
    public String createOrder(CreateOrderRequestDto dto, Integer memberId) {
        List<Map<String, Object>> itemsData = new ArrayList<>();
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (OrderItemDto item : dto.getItems()) {
            Product product = productRepository.findByProductId(item.getProductId())
                    .orElseThrow(() -> new BusinessException("商品不存在：" + item.getProductId()));

            if (product.getStatus() != 1) {
                throw new BusinessException("商品已下架：" + product.getProductName());
            }
            if (product.getQuantity() < item.getQuantity()) {
                throw new BusinessException("商品庫存不足：" + product.getProductName()
                        + "（庫存 " + product.getQuantity() + "，欲購 " + item.getQuantity() + "）");
            }

            BigDecimal standPrice = product.getPrice();
            BigDecimal itemPrice = standPrice.multiply(BigDecimal.valueOf(item.getQuantity()));
            totalPrice = totalPrice.add(itemPrice);

            Map<String, Object> itemMap = new LinkedHashMap<>();
            itemMap.put("productId", item.getProductId());
            itemMap.put("quantity", item.getQuantity());
            itemMap.put("standPrice", standPrice);
            itemMap.put("itemPrice", itemPrice);
            itemsData.add(itemMap);
        }

        // 產生訂單編號（Ms + yyyyMMdd + 6位亂數）
        String orderId = generateOrderId();

        // 序列化 items 為 json 傳入 SP
        String itemsJson;
        try {
            itemsJson = objectMapper.writeValueAsString(itemsData);
        } catch (JsonProcessingException e) {
            throw new BusinessException("建立訂單失敗：參數序列化錯誤");
        }

        final BigDecimal finalTotalPrice = totalPrice;
        final String finalItemsJson = itemsJson;
        try {
            jdbcTemplate.execute(
                    "CALL create_order(?, ?, ?, ?, ?)",
                    (CallableStatement cs) -> {
                        cs.setString(1, orderId);
                        cs.setInt(2, memberId);
                        cs.setBigDecimal(3, finalTotalPrice);
                        cs.setString(4, finalItemsJson);
                        cs.setString(5, dto.getRemark());
                        cs.execute();
                        return null;
                    }
            );
        } catch (DataAccessException e) {
            String msg = e.getMostSpecificCause().getMessage();
            if (msg != null && (msg.contains("庫存不足") || msg.contains("不存在") || msg.contains("已下架"))) {
                throw new BusinessException(msg);
            }
            throw new BusinessException("建立訂單失敗，請稍後再試");
        }

        return orderId;
    }

    @Override
    public List<OrderResponseDto> getMyOrders(Integer memberId) {
        List<Order> orders = orderRepository.findByMemberIdOrderByInptimeDesc(memberId);
        if (orders.isEmpty()) {
            return Collections.emptyList();
        }

        List<String> orderIds = orders.stream().map(Order::getOrderId).collect(Collectors.toList());

        // 一次 batch 查詢所有相關明細
        Map<String, List<OrderDetail>> detailsMap = orderDetailRepository
                .findByOrderIdIn(orderIds).stream()
                .collect(Collectors.groupingBy(OrderDetail::getOrderId));

        // 一次 batch 查詢所有相關商品
        Set<String> productIds = detailsMap.values().stream()
                .flatMap(List::stream)
                .map(OrderDetail::getProductId)
                .collect(Collectors.toSet());
        Map<String, Product> productMap = productRepository.findByProductIdIn(productIds).stream()
                .collect(Collectors.toMap(Product::getProductId, p -> p));

        return orders.stream().map(order -> {
            OrderResponseDto dto = new OrderResponseDto();
            dto.setOrderId(order.getOrderId());
            dto.setTotalPrice(order.getTotalPrice());
            dto.setPayStatus(order.getPayStatus());
            dto.setOrderStatus(order.getOrderStatus());
            dto.setRemark(order.getRemark());
            dto.setInptime(order.getInptime());

            List<OrderDetailResponseDto> details = detailsMap
                    .getOrDefault(order.getOrderId(), Collections.emptyList())
                    .stream()
                    .map(d -> toDetailDto(d, productMap))
                    .collect(Collectors.toList());
            dto.setDetails(details);
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public List<AdminOrderResponseDto> getAllOrders() {
        List<Order> orders = orderRepository.findAllByOrderByInptimeDesc();

        Set<Integer> memberIds = orders.stream().map(Order::getMemberId).collect(Collectors.toSet());
        Map<Integer, String> memberUsernameMap = memberRepository.findAllById(memberIds).stream()
                .collect(Collectors.toMap(Member::getMemberId, Member::getUsername));

        return orders.stream().map(order -> {
            AdminOrderResponseDto dto = new AdminOrderResponseDto();
            dto.setOrderId(order.getOrderId());
            dto.setMemberId(order.getMemberId());
            dto.setMemberUsername(memberUsernameMap.getOrDefault(order.getMemberId(), ""));
            dto.setTotalPrice(order.getTotalPrice());
            dto.setPayStatus(order.getPayStatus());
            dto.setOrderStatus(order.getOrderStatus());
            dto.setRemark(order.getRemark());
            dto.setInptime(order.getInptime());
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public List<OrderDetailResponseDto> getOrderDetails(String orderId) {
        orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new BusinessException("訂單不存在"));

        List<OrderDetail> details = orderDetailRepository.findByOrderId(orderId);

        Set<String> productIds = details.stream()
                .map(OrderDetail::getProductId).collect(Collectors.toSet());
        Map<String, Product> productMap = productRepository.findByProductIdIn(productIds).stream()
                .collect(Collectors.toMap(Product::getProductId, p -> p));

        return details.stream()
                .map(d -> toDetailDto(d, productMap))
                .collect(Collectors.toList());
    }

    /**
     * 產生訂單編號：Ms + yyyyMMdd + 6位亂數
     */
    private String generateOrderId() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int random = ThreadLocalRandom.current().nextInt(100000, 999999);
        return "Ms" + date + random;
    }

    private OrderDetailResponseDto toDetailDto(OrderDetail d, Map<String, Product> productMap) {
        OrderDetailResponseDto dto = new OrderDetailResponseDto();
        dto.setOrderItemSn(d.getOrderItemSn());
        dto.setProductId(d.getProductId());
        Product product = productMap.get(d.getProductId());
        dto.setProductName(product != null ? product.getProductName() : "");
        dto.setQuantity(d.getQuantity());
        dto.setStandPrice(d.getStandPrice());
        dto.setItemPrice(d.getItemPrice());
        return dto;
    }
}
