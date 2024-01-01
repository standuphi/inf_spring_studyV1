package com.jpabook.jpa.shop.Repository.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class OrderQueryRepo {
    private final EntityManager em;

    public List<OrderQueryDto> findOrderQueryDtos() {
        List<OrderQueryDto> result = findOrders();

        // collection 조회하며 더함
        // 1:다 관계에서 join역시 row가 증가하는 구조(user가 한번 더 나오는 DB구조)
        // ~ToOne만 빼서 루프를 돌린다
        result.forEach(o -> {
            List<OrderItemQueryDto> orderItems = findOrderItems(o.getOrderId()); // 영속성 루프 1회
            o.setOrderItems(orderItems); // lAZY초기화, query는 orderItem이랑 findOrderItems랑 조회, 2회
        } );
        return result; // 총 table에 접근하는 usera userb에 대한 2회 => 3회를 조회한 것(n+1)
    }

    // DTO를 JPA spec으로 보내기
    public List<OrderQueryDto> findAllByDto_optimization() {
        List<OrderQueryDto> result = findOrders();

        // map에 올려 key와 valsue로 조회하는 메모리 상에 올려둔다
        // 다만 영속성에 접근하지 않는 데이터는 Lazy 이지도 않으니 아에 메모리에 올리고 루프를 돌리지 않음
        // 메모리는 JVM에 올린 것
        Map<Long, List<OrderItemQueryDto>> orderItemMap = findOrderItemMap(toOrderIds(result));

        // 루프를 돌면서 메모리에 올려진 쿼리를 체크
        // 그 후 재조립을 함
        result.forEach(o -> o.setOrderItems(orderItemMap.get(o.getOrderId())));

        return result;
    }

    // query문이 들어오도록 한 구조, JPA에 DTO를 보내며 생성자가 map으로 올라가지 않고 DB에 올라가는 구조
    // queryDto를 대상으로 메서드를 짜기 때문에 VvDto가 받는 구조로 바꿔야 함
    // 따라서 paging이 불가능
    // 쉽게 알아듣기 : join을 하면 :다 에 맞춰서 order나 item의 table이 많아진 상태로 join되는 구조가 됨
    /*
    public List<OrderQueryDto> findAllByDto_Vv() {
        return em.createQuery("select new com.jpabook.jpa.shop.Repository.query.OrderVvDto(o.id, m.name, o.orderDate, o.status, d.address, i.name, oi.orderPrice, oi.count)" +
                " from Order o" + " join o.member" + " join o.delivery d" + " join o.orderItems oi" + " join oi.item i", OrderVvDto.class).getResultList();
    }
    */


    // 1:다 지정 <~ToOne>
    private List<OrderQueryDto> findOrders() {
        // 데이터가 대량으로 stream하기 때문에 query문이 강하다
        return em.createQuery(
                "select new com.jpabook.jpa.shop.Repository.query.OrderQueryDto(o.id, m.name, o.orderDate, o.status, d.address)" +
                        " from Order o" + " join o.member m" + " join o.delivery d", OrderQueryDto.class).getResultList();
    }

    // 동일 <OneTo~>
    // row수 증가하므로 최적화를 별도의 메서드로 한 것
    private List<OrderItemQueryDto> findOrderItems(Long orderId) {
        // orderItem의 상위는 OrderItem, ToOne관계로서 item은 row가 즹가하지 않을 것이며
        // 따라서 join oi.item.i 로 빼놓은 것
        return em.createQuery(
                "select new com.jpabook.jpa.shop.Repository.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)" +
                        " from OrderItem oi" + " join oi.item i" + " where oi.order.id = :orderId", OrderItemQueryDto.class).setParameter("orderId", orderId).getResultList();
    }

    // 내부에서 정의, collection data로 변경
    private List<Long> toOrderIds(List<OrderQueryDto> result) {
        // 객체 result에 넘어온 데이터는 toOrderIds에 넘어감
        return result.stream().map(o -> o.getOrderId()).collect(Collectors.toList());
        // 이 경우 orderId는 userA와 userB를 확인하고 collector로 변경
    }

    private Map<Long, List<OrderItemQueryDto>> findOrderItemMap(List<Long> orderIds) {
        List<OrderItemQueryDto> orderItems = em.createQuery("select new com.jpabook.jpa.shop.Repository.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)" +
                " from OrderItem oi" + " join oi.item i" + " where oi.order.id in :orderIds", OrderItemQueryDto.class).setParameter("orderIds", orderIds).getResultList();
        // queryDto가 orderId를 map으로 적용하게 변화, 이 경우 key는 orderId가 되고 value는 queryDto 객체가 되는 것
        return orderItems.stream().collect(Collectors.groupingBy(OrderItemQueryDto::getOrderId));
    }
}
