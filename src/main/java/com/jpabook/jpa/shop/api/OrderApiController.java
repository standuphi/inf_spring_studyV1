package com.jpabook.jpa.shop.api;

import com.jpabook.jpa.shop.Repository.OrderRepo;
import com.jpabook.jpa.shop.Repository.OrderSearch;
import com.jpabook.jpa.shop.Repository.query.OrderItemQueryDto;
import com.jpabook.jpa.shop.Repository.query.OrderQueryDto;
import com.jpabook.jpa.shop.Repository.query.OrderQueryRepo;
import com.jpabook.jpa.shop.domain.Item.Address;
import com.jpabook.jpa.shop.domain.Item.Order;
import com.jpabook.jpa.shop.domain.Item.OrderItem;
import com.jpabook.jpa.shop.domain.Item.OrderStatus;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.jaxb.SpringDataJaxb;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@RestController
@RequiredArgsConstructor
public class OrderApiController {
    private OrderRepo orderRepo;
    private final OrderQueryRepo orderQueryRepo;
    private final EntityManager em;

    @GetMapping("/api/v1/orders")
    public List<Order> orderV1() {
        List<Order> A = orderRepo.findAll(new OrderSearch());
        for (Order order : A) {
            order.getMember().getName(); // 초기화해서 가져오는건 맞지만
            order.getDelivery(); // get에 의해 완전히 보여짐
            // 1대다 구조에서 orderItem을 받을 객체
            List<OrderItem> orderItems = order.getOrderItems(); // order를 조회
            /*
            for (OrderItem orderItem : orderItems) {
                // 주문을 하는 item, user 불러옴
                orderItem.getItem().getName();
            }
            */
            orderItems.stream().forEach(o -> o.getItem().getName());
        }
        return A;
    }

    @GetMapping("/api/v2/orders")
    public List<OrderDto> orderV2() {
        List<Order> orders = orderRepo.findAll(new OrderSearch());
        /*
        List<OrderDto> collect = orders.stream().map(o -> new OrderDto(o)).collect((toList()));
        // dto는 list에 묶이고 객체 o에 넘겨짐
        return collect;
        */
        return orders.stream().map(o -> new OrderDto(o)).collect(toList());
    }

    // fetch join DTO 생성 구현
    @GetMapping("/api/v3/orders")
    public List<OrderDto> orderV3() {
        List<Order> orders = orderRepo.findAllWithItem();
        List<OrderDto> result = orders.stream().map(o -> new OrderDto(o)).collect(toList());
        return result;
    }

    // collection 대상으로 값을 지정
    // collection의 문제는 query가 영속성에 보내는 수가 많은 경우에는 n+1의 문제 이전이다
    @GetMapping("/api/v3.1/orders")
    public List<OrderDto> orderVV3(@RequestParam(value="offset", defaultValue="0") int offset, @RequestParam(value="limit", defaultValue="100") int limit) {
        // offset은 보여질 페이지의 정도, limit는 최대 보낼 페이지
        List<Order> orders = orderRepo.findAllWithMemberDelivery(offset, limit);
        List<OrderDto> result = orders.stream().map(o -> new OrderDto(o)).collect(toList());
        return result;
    }

    // JPA 이용한 DTO 구현 <collection o>
    @GetMapping("/api/v4/orders")
    public List<OrderQueryDto> orderV4() {
        return orderQueryRepo.findOrderQueryDtos();
    }

    // JPA spec을 이용해 한번에 DTO를 보내기, 자동화는 좋지만 너무 어려운 구조를 가짐
    // collection을 map상에 올려둔 다는 것은 quert가 적게 보내지는 점 => 복잡도 down
    @GetMapping("/api/v5/orders")
    public List<OrderQueryDto> orderV5() {
        return orderQueryRepo.findAllByDto_optimization();
    }

    // v5의 최적화
    // join과 query 1회 로 DB에만 접근하는 구조로 가짐
    /*
    @GetMapping("/api/v6/orders")
    public List<OrderQueryDto> orderV6() {
        // DB에 중복으로 가는 데이터가 있어도 join을 이용한다,
    }
    */


    /*
    @Data // 내부에서 정의한 것을 이용하기 위해
    static class OrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private Address address;
        private OrderStatus orderStatus;
        // 다 의 관계에서 정의
        private List<OrderItem> orderItems;

        public OrderDto(Order order) {
            // 지금 이 상태는 DTO가 내부와 의존이 강해진 상태
            orderId = order.getId(); // 그대로 바인딩해서 호출했으니 당연 보임
            name = order.getMember().getName();
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();
            order.getOrderItems().stream().forEach(o -> o.getItem().getName());
            orderItems = order.getOrderItems();
        }
      */
    @Data // dto 정의를 private로 다시 wrapping 하는 것
    static class OrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private Address address;
        private OrderStatus orderStatus;
        // 다 의 관계에서 정의
        private List<OrderItemDto> orderItems;

        public OrderDto(Order order) {
            // 지금 이 상태는 DTO가 내부와 의존이 강해진 상태
            orderId = order.getId(); // 그대로 바인딩해서 호출했으니 당연 보임
            name = order.getMember().getName();
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();
            orderItems = order.getOrderItems().stream().map(orderItem -> new OrderItemDto(orderItem)).collect(toList());
        }
    }
    @Data // wrapping 시키는 orderItemDto
    static class OrderItemDto {
        // 노출되지 않을 건 private로 묶이지 않았으니 java에 의해 공개되지도 않음
        private String itemName;
        private int orderPrice;
        private int count;
        public OrderItemDto(OrderItem orderItem) {
            itemName = orderItem.getItem().getName();
            orderPrice = orderItem.getOrderPrice();
            count = orderItem.getCount();
        }
    }
}