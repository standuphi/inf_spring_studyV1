package com.jpabook.jpa.shop.Repository;

import com.jpabook.jpa.shop.domain.Item.Member;
import com.jpabook.jpa.shop.domain.Item.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepo {
    private final EntityManager em;

    public void save(Order order) {
        em.persist(order);
    } // 영구성에 들어갔다 나온 것을 고려
    public Order findOne(Long id) {
        return em.find(Order.class, id);
    }

    /*
    public List<Order> findAll(OrderSearch orderSearch) {
        // 반드시 조건문의 + 경우 양 옆의 space는 중요!!
        return em.createQuery("select o from Order o join o.member m" +
                        " where o.status = :status " + " and m.name like :name ", Order.class)
                        .setParameter("status", orderSearch.getOrderStatus())
                        .setParameter("name", orderSearch.getMemberName())
                        // 최대 페이지, 최소 스타트
                        .setFirstResult(100)
                        .setMaxResults(1000)
                        .getResultList();
    }
    */

    public List<Order> findAll(OrderSearch orderSearch) {
        // 동적으로 범주를 넓게 잡음
        String jpql = "select o from Order o join 0.member m";
        boolean isFirstCondition = true;

        // 주문 검색
        if (orderSearch.getOrderStatus() != null) {
            if (isFirstCondition) {
                // 일치한다면 조건문은 where에 진행
                jpql += "where";
            } else {
                // true라는 것은 반드시 where로 빠질수 있는 도구
                jpql += "and";
            }
            jpql += "0.status = :status";
        }
        // 회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            // spring에서 지원하는 StringUtils를 이용
            if (isFirstCondition) {
                if (isFirstCondition) {
                    jpql += "where";
                } else {
                    jpql += "and";
                }
                jpql += "m.name like :name"; // 동적으로 넘기는 코드
            }
        }
        // 동적이 충족되는 대 범주의 구현
        TypedQuery<Order> q = em.createQuery(jpql, Order.class)
                .setFirstResult(100)
                .setMaxResults(1000); // return해줄 객체가 없기 떄문에 동적객체를 생성 => q
        // .getResultList();

        if (orderSearch.getOrderStatus() != null) {
            q = q.setParameter("status", orderSearch.getOrderStatus());
        }
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            q = q.setParameter("name", orderSearch.getMemberName());
        }
        return q.getResultList();
        // 실제 DB로 넘겨짐을 알림 혹은 list없이 return q 진행
    }

    public List<Order> findAllCriteria(OrderSearch orderSearch) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Order> cq = cb.createQuery(Order.class);
        Root<Order> o = cq.from(Order.class);
        Join<Order, Member> m = o.join("member", JoinType.INNER);

        List<Predicate> criteria = new ArrayList<>();

        // 주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            // 동적으로 명시해서 영구성으로 있는Repo에서 정의
            // private final인 시점에서 영구성을 한번 지나가는 것
            Predicate status = cb.equal(o.get("status"), orderSearch.getOrderStatus());
            // id가 기록된 것이 없으면 등록하고 return없이 진행
            criteria.add(status);
        }

        // 회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            Predicate name = cb.like(m.<String>get("name"), "%" + orderSearch.getMemberName() + "%");
            criteria.add(name);
        }

        // Order에게 최종 결과 반환
        cq.where(cb.and(criteria.toArray(new Predicate[criteria.size()])));
        TypedQuery<Order> query = em.createQuery(cq).setFirstResult(100).setMaxResults(1000);
        return query.getResultList();
    }

    // fetchjoin을 위한 생성 메서드
    public List<Order> findAllWithMembberDelivery() {
        return em.createQuery("select o from Order o" + " join fetch o.member m"
        + " join fetch o.delivery d", Order.class).getResultList();
    }

    // jpa를 통해 DTO를 바로 조회하는 것
    // 메서드 기능으로 수행하기 떄문에 DTO로 정의한 것을 찾아 List<>로 선언한 것이 현재
    public List<OrderSingleQueryDto> findOrderDtos() {
        // 고려할 것은 객체 o로 지정하면 o를 찾게되는 것이므로 명시적으로 선언할 것
        return em.createQuery("select new com.jpabook.jpa.shop.Repository.OrderSingleQueryDto(o.id, m.name, o.orderDate, o.status, d.address)"
        + " from Order o" + " join fetch o.member m" + " join fetch o.delivery d", OrderSingleQueryDto.class).getResultList();
    }
    
    // 다 관계 fetch join 구현
    public List<Order> findAllWithItem() {
        // Order의 재사용성으로 정의함
        return em.createQuery("select distinct o from Order o" +
                " join fetch o.member m" + " join fetch o.delivery d" + " join fetch o.orderItems oi" + " join fetch oi.item i", Order.class).getResultList();
    }

    // 페이징 돌파 DTO 생성
    public List<Order> findAllWithMemberDelivery(int offset, int limit) {
        return em.createQuery("select o from Order o" + " join fetch o.member m" + " join fetch o.delivery d", Order.class).setFirstResult(offset).setMaxResults(limit).getResultList();
    }
}
    /*
    // JPA criteria <자바를 통한 jpql 수작업 위와의 차이>
    /*
    public List<Order> findAllByCriteria(OrderSearch.getOrderStatus) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Order> cq = cb.createQuery(Order.class);
        Root<Order> o = cq.from(Order.class);
        Join<Object, Object> m = o.join("member", JoinType.INNER);

        List<Predicate> criteria =new ArrayList<>();

        // 주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            Predicate status = cb.equal(o.get("status"), orderSearch.getOrderStatus());
            criteria.add(status);
        }

        // 회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            Predicate name = cb.like(m.<String>get("name"), "%" + orderSearch.getMemberName() + "%");
            criteria.add(name);
        }

        // criteria 조건을 add하는 구현
        cq.where(cb.and(criteria.toArray(new Predicate[criteria.size()])));
        TypeQuery<Order> qq = em.createQuery(cq).setFirstResult(100).setMaxResults(1000);
        return cq.getResultList();
    }
    */
