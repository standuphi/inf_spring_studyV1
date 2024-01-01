package com.jpabook.jpa.shop.Repository;

import com.jpabook.jpa.shop.domain.Item.Address;
import com.jpabook.jpa.shop.domain.Item.OrderStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderSingleQueryDto {
    private Long orderId;
    private String name;
    private LocalDateTime orderDate;
    private OrderStatus orderStatus;
    private Address address;

    // 생성 DTO
    public OrderSingleQueryDto(Long orderId, String name, LocalDateTime orderDate, OrderStatus orderStatus, Address address) {
        // 명시가 잘된 case, 유지보수 up!!
        this.orderId = orderId;
        this.name = name;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
        this.address = address;
    }
}
