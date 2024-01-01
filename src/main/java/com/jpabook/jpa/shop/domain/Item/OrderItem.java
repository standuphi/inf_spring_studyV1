package com.jpabook.jpa.shop.domain.Item;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
public class OrderItem {
    // 해당 테이블은 item과 order, orderPrice, count를 instance로 함
    @Id @GeneratedValue
    @Column(name="order_item_id")
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="item_id")
    private Item item; // ~toOne은 Item class에 @BatchSize 작은 관계로 설정

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="order_id") // order를 받는 각각의 객체는 각각 1개씩
    private Order order;
    // 회원 테이블에서 orderItem은 order_id인 id로부터 FK를 받음

    private int orderPrice; // 주문 시 가격
    private int count;

    // 생성 메서드은 반드시 도움이 된다
    // 여기서 ... orderItems로 정의된 이유로 어떤 것을 호출할지 경우가 구분되기 떄문
    public static OrderItem createOrderItem(Item item, int orderPrice, int count) {
        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setOrderPrice(orderPrice);
        orderItem.setCount(count);
        item.removeStock(count);
        return orderItem;
    }

    //비즈니스 로직에 따라 생성자 구현
    public void cancel() {
        getItem().addStock(count); // 주문한 크기만큼 재고에 맞는 크기로 반환

    }
    public int getTotalPrice() {
        return getOrderPrice() * getCount();
    }
}
