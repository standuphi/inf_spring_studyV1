package com.jpabook.jpa.shop.Service;

import com.jpabook.jpa.shop.Repository.ItemRepo;
import com.jpabook.jpa.shop.Repository.MemberRepo;
import com.jpabook.jpa.shop.Repository.OrderRepo;
import com.jpabook.jpa.shop.Repository.OrderSearch;
import com.jpabook.jpa.shop.domain.Item.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {
    private final MemberRepo memberRepo;
    private final OrderRepo orderRepo;
    private final ItemRepo itemRepo;

    // 주문
    @Transactional
    public Long order(Long memberId, Long itemId, int count) {
        // 엔티티 조회
        Member member = memberRepo.findOne(memberId);
        Item item = itemRepo.findOne(itemId);

        // 배송정보 조회
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());
        delivery.setStatus(DeliveryStatus.READY);

        // 주문상품 생성
        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);

        //주문 생성
        Order order = Order.createOrder(member, delivery, orderItem);

        // 주문 저장 => 영속성에 들어온 것으로 다른 class들도 persist 자동
        // cascade의 기능
        orderRepo.save(order);
        return order.getId();
    }

    // 취소
    @Transactional
    public void cancelOrder(Long orderId) {
        // 주문 entity 조회
        Order order = orderRepo.findOne(orderId);

        // 배송 신청이 들어간 상태에서는 변경이 안되고 바로 cancel로 이어지는 비즈니스 로직을 기억
        order.cancel();
    }

    // 검색
    // 주문이 다중 선택으로 들어간 경우 주문상툼 코드에 옵션을 부여해야 함
    /*
    public List<Order> findOrders(OrderSearch orderSearch) {
        return orderRepo.findAll(orderSearch);
    } => 해당 부분은 변경감지에 의한 권한 확인*/
    public List<Order> findOrders(OrderSearch orderSearch) {
        return orderRepo.findAll(orderSearch);
    }
}
