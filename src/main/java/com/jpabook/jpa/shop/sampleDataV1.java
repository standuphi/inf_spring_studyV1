package com.jpabook.jpa.shop;

import com.jpabook.jpa.shop.domain.Item.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;

@Component
@RequiredArgsConstructor
public class sampleDataV1 {
    // User 2명이 주문을 실행한 경우를 예시로 구현
    // <InitDb>
    private final InitService initService;

    @PostConstruct
    public void init() {
        initService.dbInit1();
    }

    // spring lifecyccle에 의해 새로운 bean으로 관리
    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitService {
        private final EntityManager em;
        public void dbInit1() {
            Member member = createMember("user1", "seoul", "1", "1010");
            em.persist(member);

            // 이후부터는 flush하면서 update 쿼리문을 날려질 data를 구현
            Bookobj book1 = createBook("book name1", 10000, 100);
            em.persist(book1);

            Bookobj book2 = createBook("book name2", 20000, 100);
            em.persist(book2);

            // Item1 Item2 정의2
            OrderItem orderItem1 = OrderItem.createOrderItem(book1, 10000, 1);
            OrderItem orderItem2 = OrderItem.createOrderItem(book2, 20000, 1);
            Order order = Order.createOrder(member, createDelivery(member));
            em.persist(order); // flush에서 불러옴 => 주문 상품이 delivery에도 나와야 함
        }

        public void dbInit2() {
            Member member = createMember("user2", "seoul", "1", "2020");
            em.persist(member);

            Bookobj book1 = createBook("book name3", 10000, 200);
            em.persist(book1);

            Bookobj book2 = createBook("book name4", 40000, 100);
            em.persist(book2);

            OrderItem orderItem1 = OrderItem.createOrderItem(book1, 10000, 3);
            OrderItem orderItem2 = OrderItem.createOrderItem(book2, 40000, 4);
            Order order = Order.createOrder(member, createDelivery(member));
            em.persist(order);
        }

        private Member createMember(String name, String city, String street, String zipcode) {
            Member member = new Member();
            member.setName(name);
            member.setAddress(new Address(city, street, zipcode));
            return member;
        }
        private Bookobj createBook(String name, int price, int stockQuantity) {
            Bookobj book = new Bookobj();
            book.setName(name);
            book.setStockQuantity(stockQuantity);
            return book;
        }
        private Delivery createDelivery(Member member) {
            // 주문 상품을 조회할수 있도록 Order class에 메서드를 부여
            Delivery delivery = new Delivery();
            delivery.setAddress(member.getAddress());
            return delivery;
        }
    }
}
