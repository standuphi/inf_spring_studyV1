package com.jpabook.jpa.shop.Repository;

import com.jpabook.jpa.shop.domain.Item.OrderStatus;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class OrderSearch {
    // 검색 로직에 필요한 구현, where문을 통해 접근
    private String memberName;
    private OrderStatus orderStatus;
}
