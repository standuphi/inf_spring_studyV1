package com.jpabook.jpa.shop.domain.Item;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;

@Entity
@Table(name="orders") // class와 동일한 경우 오류를 범할 수 있어서 방지용
@Getter @Setter // javabean을 이용하기 위해 어쩔수없이 안되는 부분들의 해결
public class Order {
    // getter와 setter로 양방향 FK를 instance로 가지는 table

    @Id
    @GeneratedValue
    @Column(name="order_id")

    // 양방향에서 order와 member 관계이
    private Long id; // FK므로 order는 다대일 member는 일대다
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="member_id") // name으로 지정한 것은 일종의 FK라 생각해보자
    private Member member; // Member 클래스의 name="member"를 접근
    // 주인이 되는 입장에서는 FK를 하나만 받아도 되는데 둘 중에 누구를 선택해야 하는가?
    // 결론은 자기 table에서 변경되는 instance에 맞춰서 컴퓨터에게 인지 시키는 것
    // 회원 entity는 order를 주인으로 누가 order를 변경하는지에 선택
    // 주인이 없기 떄문에 영구성에서 오류가 발생됨

    // @BatchSize(1000) => property 대신 사용가능, 명시적으로 알리는 용도
    @OneToMany(mappedBy="order", cascade=CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    // @JsonIgnore 1:다 는 lazy를 기본으로 수행함
    @OneToOne(fetch=FetchType.LAZY, cascade=CascadeType.ALL) // FK를 정의
    @JoinColumn(name="delivery_id")
    private Delivery delivery;
    private LocalDateTime orderDate; // 주문시간

    @Enumerated(EnumType.STRING)
    private OrderStatus status; // 주문상태 받기

//    public static void main(String[] args) {
//        Member member = new Member();
//        Order order = new Order();
//
//        member.getOrders().add(order);
//        order.setMember(member);
//    }

    public void setMember(Member member) {
        this.member = member;
        member.getOrders().add(this);
    }

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
        delivery.setOrder(this);
    }

    // 생성 메서드 구현
    public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems) {
        // 회원정보와 배송정보 그리고 item기능의 여러개를 의미하는 ...
        Order order = new Order();
        order.setMember(member);
        order.setDelivery(delivery);

        for (OrderItem orderItem : orderItems) {
            order.addOrderItem(orderItem);
        }
        order.setStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());
        return order;
    }
    // 생성 로직대로 주문 시 사용자 정보와 주문시간을 기록

    // 재고가 있는지 없는지 파악하기 위해 비즈니스 로직 구현
    public void cancel() {
        if (delivery.getStatus() == DeliveryStatus.COMP) {
            throw new IllegalStateException("주문이 들어간 상태입니다");
        }
        // 주문이 들어갔으므로 취소요청을 불가능하다

        this.setStatus(OrderStatus.CANCEL);
        for (OrderItem orderItem: orderItems) {
            orderItem.cancel();
        }
        // 취소 요청이 동시에 일어나는 경우 구현
    }

    // 전체 주문 가격 조회
    public int getTotalPrice() {
        int totalPrice = 0;
        for (OrderItem orderItem: orderItems) {
            totalPrice += orderItem.getTotalPrice();
        }
        return totalPrice;
    }
}
