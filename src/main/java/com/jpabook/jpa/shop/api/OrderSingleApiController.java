package com.jpabook.jpa.shop.api;

import com.jpabook.jpa.shop.Repository.OrderRepo;
import com.jpabook.jpa.shop.Repository.OrderSearch;
import com.jpabook.jpa.shop.Repository.OrderSingleQueryDto;
import com.jpabook.jpa.shop.domain.Item.Address;
import com.jpabook.jpa.shop.domain.Item.Order;
import com.jpabook.jpa.shop.domain.Item.OrderStatus;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class OrderSingleApiController {
    // api를 개발하는데 있어 성능의 차이를 확인
    // 어떻게 작성하는가에 따라 server가 응답한 결과의 속도가 다름
    private final OrderRepo orderRepo;

    // 다대일, 일대일 관계에서 mapping이 이뤄지는 경우
    // 설정으로 ..
    // entity가 프록시에 있다?
    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1() {
        List<Order> vv1 = orderRepo.findAll(new OrderSearch());
        // 루프에 따라 query 1개 당 주문 2개 생성
        for (Order order : vv1) {
            order.getMember().getName(); // LAZY에 의한 강제 초기화 <영속성에 없다면>
            order.getDelivery().getAddress(); // LAZY에 의한 강제 초기화
            // 이떄 영속성에 없던 delivery는 참가
        }
        return vv1;
    }

    // DTO를 통해 entity를 접근 => api변경이 일어나도 DTO에서 확인
    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderDto> orderV2() {
        List<Order> orders = orderRepo.findAll(new OrderSearch());
        List<SimpleOrderDto> result = orders.stream().map(o -> new SimpleOrderDto(o)).collect(Collectors.toList());
        // 객체 o는 simple뭐시기로 바뀌고 collect를 통해 list로 반환
        return result;
    }

    // fetch join DTO
    @GetMapping("/api/v3/simple-orders")
    public List<SimpleOrderDto> orderV3() {
        List<Order> orders = orderRepo.findAllWithMembberDelivery();
        List<SimpleOrderDto> result = orders.stream().map(o -> new SimpleOrderDto(o)).collect(Collectors.toList());
        return result;
    }

    // JPA에 반환하는 조회DTO 생성
    @GetMapping("/api/v4/simple-orders")
    public List<OrderSingleQueryDto> orderV4() {
        // 새롭게 repo로부터 쿼리문을 확인, 원하는 데이터를 조회하도록 class를 생성해서 이용한 것
        return orderRepo.findOrderDtos();
    }

    @Data // 생성 DTO를 위한 메서드 생성
    static class SimpleOrderDto {
        // 메서드 매개변수로 만들어도 가능!!
        private Long orderId;
        private String name;
        private LocalDateTime orderDate; // 이 경우 query가 3개가 나가는 것이다
        private OrderStatus orderStatus;
        private Address address;

        public SimpleOrderDto(Order order) {
            // o가 객체로서 받는 것
            // 여기서 LAZY 초기화는 name 과 address, 루프를 돌떄마다 영속성을 체크
            orderId = order.getId();
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            name = order.getMember().getName();
            address = order.getDelivery().getAddress(); // delivery가 address에 넘겨짐
        }
    }

}