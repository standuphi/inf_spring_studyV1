package com.jpabook.jpa.shop.Repository.query;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

// 아에 화면에 보여줄 요소를 정의 한 것<재정의 x 정의 o>
@Data
public class OrderItemQueryDto {
    @JsonIgnore
    private Long orderId;
    private String itemName;
    private int orderPrice;
    private int count;

    // 생성 DTO
    public OrderItemQueryDto(Long orderId, String itemName, int orderPrice, int count) {
        this.orderId = orderId;
        this.itemName = itemName;
        this.orderPrice = orderPrice;
        this.count = count;
    }
}
