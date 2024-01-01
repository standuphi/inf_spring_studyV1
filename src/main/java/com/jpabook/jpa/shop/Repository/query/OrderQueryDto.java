package com.jpabook.jpa.shop.Repository.query;

import com.jpabook.jpa.shop.domain.Item.Address;
import com.jpabook.jpa.shop.domain.Item.OrderItem;
import com.jpabook.jpa.shop.domain.Item.OrderStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(of = "orderId") // FK로서 화면에 필요한 요소를 다시 끌어온 것
public class OrderQueryDto {
    private Long orderId;
    private String name;
    private LocalDateTime orderDate;
    private OrderStatus orderStatus;
    private Address address;
    private List<OrderItemQueryDto> orderItems; // OrderQueryDto 메서드를 한번 지나감, 동시에 findOrders spec이 변경됨

    // 생성 DTO <비즈니스 로직>
    public OrderQueryDto(Long orderId, String name, LocalDateTime orderDate, OrderStatus orderStatus, Address address) {
        this.orderId = orderId;
        this.name = name;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
        this.address = address;

        // DTO 생성자가 OrderQueryDto에 접근하고 내부 instance에 접근
        // 생성자가 DTO에 접근해서 query가 1회 나가는 구조가 되지만 작업량과 spec 이슈가 크다
        // this.orderItems = orderItems; // List<OrderItemQueryDto> orderItems -> 인자
    }
}
