package com.jpabook.jpa.shop.Service;

import com.jpabook.jpa.shop.Repository.OrderRepo;
import com.jpabook.jpa.shop.domain.Item.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

// 정적으로 사용할수 있도록 메서드 지원 static
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest
@Transactional
@ExtendWith(SpringExtension.class) // junit5에서의 RunWtih, SpringRunner

public class OrderServiceTest {
    // 단위테스트가 정확하지만 우선은 공부 목적으로 진행

    @PersistenceContext
    EntityManager em;
    @Autowired OrderService orderService;
    @Autowired OrderRepo orderRepo;

    @Test
    public void ProductOrder() throws Exception {
        // given
        Member member = createMember();
        Item item = createBook("시골 jpa", 10000, 10); //

        int orderCount = 2; // 조건에 지정하기 위해서, 게속해서 수정

        // when
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        // then
        Order getOrder = orderRepo.findOne(orderId); // 조건에 해당되는 식별을 인식하도록
        assertEquals("상품 주문시 상태", OrderStatus.ORDER, getOrder.getStatus()); // getStatus는 실제값
        assertEquals("상품 종류가 정확함", 1, getOrder.getOrderItems().size());
        assertEquals("상품 주문의 가격 * 수량", 10000 * 2, getOrder.getTotalPrice()); // int가 정확히 정의가 안되서 그럼
        assertEquals("상품 주문 후 재고가 줌", 8, book.getStockQuantity()); // 반환되는 값에 따라 테스트 실패일지 정답일지 파악
    }

    // exception이 터지는 상황으로 orderItem의 remove가 마이너스가 되어 item의 remove가 발생, exception 파일로 넘어가는 경우
    @Test
    public void ProductOrderOverCancel() throws Exception {
        // given
        Member member = createMember();
        Item item = createBook("시골 jpa", 10000, 10);

        int orderCount = 11;

        // when
        orderService.order(member.getId(), item.getId(), orderCount);

        // then <when에서 거치지 못하고 수행된 경우>
        fail("예외 발생 code1234");
    }

    @Test
    public void ProductCancel() throws Exception {
        // given
        Member member = createMember();
        Item item = createBook();

        int orderCount = 2;

        Long orderId = orderService.order(member.getId(), item.getId(), orderCount); // 이미 주문은 준비된 상태

        // when
        orderService.cancelOrder(orderId);

        // then
        Order getOrder = orderRepo.findOne(orderId);
        assertEquals("주문 취소 시 상태", OrderStatus.CANCEL, getOrder.getStatus());
        assertEquals("당연히 재고는 증가", 10, item.getStockQuantity());
    }

    private Member createMember() {
        Member member = new Member();
        member.setName("회원1");
        member.setAddress(new Address("seoul", "kawa", "123-123"));
        em.persist(member); // 테스트용으로 영구성에 보내고 확인

        // 반드시 return으로 내부에서 정의되도록 함
        return member;
    }
    private Bookobj createBook(String name, int price, int stockQuantity) {
        Bookobj book = new Bookobj();
        book.setName(name);
        book.setPrice(stockQuantity);
        book.setStockQuantity(price);
        em.persist(book);

        return book;
    }
}
